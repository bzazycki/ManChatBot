"""
Author: Ethan Armbruster
Contact Email: armbrue2@miamioh.edu
"""

from flask import Flask, request, jsonify
from openai import OpenAI
import os
import sqlite3
import json
import traceback
from datetime import datetime
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

# Initialize Flask app
app = Flask(__name__)

# Global variable to store persistent context
persistent_context = ""

# Load context from a .txt file (only once during startup)
def loadPersistentContext(file_path):
    try:
        with open(file_path, 'r') as file:
            return file.read()
    except FileNotFoundError:
        return ""

# Load persistent context at startup
persistent_context = loadPersistentContext("context.txt")

# Function to get the model from the database
def getModel():
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("SELECT model FROM ai LIMIT 1")
        model = cursor.fetchone()
        conn.close()
        if model:
            return model[0]
        else:
            return None
    except Exception as e:
        return None

# Function to get the API key from the database
def getAPIKey():
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("SELECT key FROM ai LIMIT 1")
        api_key = cursor.fetchone()
        conn.close()
        return api_key[0]
    except Exception as e:
        return None

# Function to get the context from the database
def getContext():
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("SELECT context FROM ai LIMIT 1")
        context = cursor.fetchone()
        conn.close()
        if context:
            return context[0]
        else:
            return ""
    except Exception as e:
        return ""

# Function to get the email user from the database
def getEmailUser():
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("SELECT emailUser FROM ai LIMIT 1")
        email_user = cursor.fetchone()
        conn.close()
        return email_user[0]
    except Exception as e:
        return None

# Function to get the email password from the database
def getEmailPass():
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("SELECT emailPass FROM ai LIMIT 1")
        email_pass = cursor.fetchone()
        conn.close()
        return email_pass[0]
    except Exception as e:
        return None

# Function to set the email user in the database
def setEmailUser(email_user):
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET emailUser = ? WHERE id = 1", (email_user,))
        conn.commit()
        conn.close()
    except Exception as e:
        print(f"Error setting email user: {e}")
        traceback.print_exc()

# Function to set the email password in the database
def setEmailPass(email_pass):
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET emailPass = ? WHERE id = 1", (email_pass,))
        conn.commit()
        conn.close()
    except Exception as e:
        print(f"Error setting email password: {e}")
        traceback.print_exc()

# Function to log the chat GPT call to the database
def dbLog(user_message, completion):
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        timestamp = datetime.now().isoformat()
        response_json = json.dumps(completion, default=str)
        cursor.execute("INSERT INTO history (input, output, timestamp) VALUES (?, ?, ?)",
                       (user_message, response_json, timestamp))
        conn.commit()
        conn.close()
        print("Data logged successfully")  # Debug print
    except Exception as e:
        print(f"Error logging chat GPT call: {e}")
        traceback.print_exc()  # Print the full traceback for debugging

# Initialize the OpenAI client with the API key from the database
client = OpenAI(
    api_key=getAPIKey()
)

def hash_api_key(api_key):
    """
    Hash the API key so that everything besides the 'sk-proj' prefix and the last four characters are '*'.
    
    @param api_key: The original API key.
    @return: The hashed API key.
    """
    if api_key.startswith("sk-proj") and len(api_key) > 10:
        return "sk-proj" + "*" * (len(api_key) - 10) + api_key[-4:]
    return "*" * len(api_key)

@app.route('/getModel', methods=['GET'])
def getModelRoute():
    """
    API endpoint to get the current model from the database.
    
    @return: JSON response containing the current model or an error message.
    """
    try:
        model = getModel()
        if model:
            return jsonify({"model": model}), 200
        else:
            return jsonify({"error": "Model not found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/getApiKey', methods=['GET'])
def getAPIKeyRoute():
    """
    API endpoint to get the current API key from the database.
    
    @return: JSON response containing the current API key or an error message.
    """
    try:
        api_key = getAPIKey()
        if api_key:
            hashed_api_key = hash_api_key(api_key)
            return jsonify({"api_key": hashed_api_key}), 200
        else:
            return jsonify({"error": "API key not found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/getContext', methods=['GET'])
def get_context_route():
    """
    API endpoint to get the current context from the database.
    
    @return: JSON response containing the current context or an error message.
    """
    try:
        context = getContext()
        if context:
            return jsonify({"context": context}), 200
        else:
            return jsonify({"context": ""}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/updateModel', methods=['POST'])
def update_model():
    """
    API endpoint to update the model in the database.
    
    @param request: Flask request object containing JSON payload with the new model.
    @return: JSON response indicating success or error message.
    """
    try:
        data = request.get_json()
        new_model = data.get("model", "")
        
        if not new_model:
            return jsonify({"error": "No model provided"}), 400

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET model = ? WHERE id = 1", (new_model,))
        conn.commit()
        conn.close()

        return jsonify({"message": "Model updated successfully"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/updateApiKey', methods=['POST'])
def update_api_key():
    """
    API endpoint to update the API key in the database.
    
    @param request: Flask request object containing JSON payload with the new API key.
    @return: JSON response indicating success or error message.
    """
    try:
        data = request.get_json()
        new_api_key = data.get("api_key", "")
        
        if not new_api_key:
            return jsonify({"error": "No API key provided"}), 400

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET key = ? WHERE id = 1", (new_api_key,))
        conn.commit()
        conn.close()

        return jsonify({"message": "API key updated successfully"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/updateContext', methods=['POST'])
def update_context():
    """
    API endpoint to update the context in the database.
    
    @param request: Flask request object containing JSON payload with the new context.
    @return: JSON response indicating success or error message.
    """
    try:
        data = request.get_json()
        new_context = data.get("context", "")
        
        if not new_context:
            return jsonify({"error": "No context provided"}), 400

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET context = ? WHERE id = 1", (new_context,))
        conn.commit()
        conn.close()

        return jsonify({"message": "Context updated successfully"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/chatbot', methods=['POST'])
def chatbot():
    """
    API endpoint to handle user messages and return AI responses.
    
    @param request: Flask request object containing JSON payload with user message.
    @return: JSON response containing AI response or error message.
    """
    try:
        data = request.get_json()
        user_message = data.get("user_message", "")
        
        if not user_message:
            return "No user message provided"

        user_input = []

        # Include the context from the database
        context = getContext()
        if context:
            user_input.append({"role": "system", "content": context})
        
        # Add the user's message
        user_input.append({"role": "user", "content": user_message})

        # Call the OpenAI API
        completion = client.chat.completions.create(
            model=getModel(),
            messages=user_input,
        )

        # Extract the AI response
        ai_response = completion.choices[0].message.content
        
        # Log the chat GPT call to the database
        dbLog(user_message, completion)
        
        # Return the AI response
        return ai_response
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/login', methods=['POST'])
def login():
    """
    API endpoint to check if the provided username and password are valid.
    
    @param request: Flask request object containing JSON payload with username and password.
    @return: JSON response indicating whether the login is successful or not.
    """
    try:
        data = request.get_json()
        username = data.get("username", "")
        password = data.get("password", "")
        
        if not username or not password:
            return jsonify({"error": "Missing username or password"}), 400

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("SELECT 1 FROM account WHERE user = ? AND pass = ?", (username, password))
        if cursor.fetchone():
            return jsonify({"valid": True}), 200
        else:
            return jsonify({"valid": False}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/sendEmail', methods=['POST'])
def send_email():
    """
    API endpoint to send an email.
    
    @param request: Flask request object containing JSON payload with 'to_email' and 'message'.
    @return: JSON response indicating success or error message.
    """
    try:
        data = request.get_json()
        to_email = data.get("to_email", "")
        message_content = data.get("message", "")
        
        if not to_email or not message_content:
            return jsonify({"error": "Missing email or message"}), 400

        sender_email = getEmailUser()
        sender_password = getEmailPass()

        emailSubject = "Your ManChatBot Chat"
        smtpServer = "smtp.gmail.com"
        smtpPort = 587
        # Create the email
        msg = MIMEMultipart()
        msg['From'] = sender_email
        msg['To'] = to_email
        msg['Subject'] = emailSubject
        msg.attach(MIMEText(message_content, 'plain'))

        # Send the email
        server = smtplib.SMTP(smtpServer, smtpPort)
        server.starttls()
        server.login(sender_email, sender_password)
        server.sendmail(sender_email, to_email, msg.as_string())
        server.quit()

        return jsonify({"message": "Email sent successfully"}), 200
    except Exception as e:
        print(f"Error sending email: {e}")
        traceback.print_exc()  # Print the full traceback for debugging
        return jsonify({"error": str(e)}), 500

@app.route('/getUser', methods=['GET'])
def get_user_route():
    """
    API endpoint to get the email user from the database.
    
    @return: JSON response containing the email user or an error message.
    """
    try:
        email_user = getEmailUser()
        if email_user:
            return jsonify({"email_user": email_user}), 200
        else:
            return jsonify({"error": "Email user not found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/getPass', methods=['GET'])
def get_pass_route():
    """
    API endpoint to get the email password from the database.
    
    @return: JSON response containing the email password or an error message.
    """
    try:
        email_pass = getEmailPass()
        if email_pass:
            return jsonify({"email_pass": email_pass}), 200
        else:
            return jsonify({"error": "Email password not found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/getEmailUser', methods=['GET'])
def get_email_user_route():
    """
    API endpoint to get the email user from the database.
    
    @return: JSON response containing the email user or an error message.
    """
    try:
        email_user = getEmailUser()
        if email_user:
            return jsonify({"email_user": email_user}), 200
        else:
            return jsonify({"error": "Email user not found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/getEmailPass', methods=['GET'])
def get_email_pass_route():
    """
    API endpoint to get the email password from the database.
    
    @return: JSON response containing the email password or an error message.
    """
    try:
        
        email_pass = getEmailPass()
        if email_pass:
            return jsonify({"email_pass": email_pass}), 200
        else:
            return jsonify({"error": "Email password not found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)
