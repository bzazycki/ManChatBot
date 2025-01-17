"""
Author: Ethan Armbruster, Zach Clouse
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

@app.route('/getAPIKey', methods=['GET'])
def getAPIKeyRoute():
    """
    API endpoint to get the current API key from the database.
    
    @return: JSON response containing the current API key or an error message.
    """
    try:
        api_key = getAPIKey()
        if (api_key):
            hashed_api_key = hash_api_key(api_key)
            return jsonify({"apiKey": hashed_api_key}), 200
        else:
            return jsonify({"error": "API key not found"}), 404
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

@app.route('/updateAPIKey', methods=['POST'])
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

@app.route('/upsertContextSection', methods=['POST'])
def upsertContextSection():
    """
    API endpoint to update a context section if it exists, or create a new one if it doesn't.
    
    @param request: Flask request object containing JSON payload with id, contextHeader, active, and contextText.
    @return: JSON response indicating success or error message.
    """
    try:
        data = request.get_json()
        context_id = data.get("id", None)
        context_header = data.get("contextHeader", "")
        active = data.get("active", False)
        context_text = data.get("contextText", "")
        
        if not context_header or not context_text:
            return jsonify({"error": "Missing contextHeader or contextText"}), 400

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()

        if context_id is not None:
            cursor.execute("SELECT 1 FROM context WHERE id = ?", (context_id,))
            if cursor.fetchone():
                cursor.execute("UPDATE context SET contextHeader = ?, active = ?, contextText = ? WHERE id = ?",
                               (context_header, active, context_text, context_id))
            else:
                cursor.execute("INSERT INTO context (contextHeader, active, contextText) VALUES (?, ?, ?)",
                               (context_header, active, context_text))
        else:
            cursor.execute("INSERT INTO context (contextHeader, active, contextText) VALUES (?, ?, ?)",
                           (context_header, active, context_text))

        conn.commit()
        conn.close()

        return jsonify({"message": "Context section upserted successfully"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/getContextSections', methods=['GET'])
def getContextSections():
    """
    API endpoint to get context sections from the database.
    
    @param getAll: Boolean query parameter to determine if all context sections should be returned or only active ones.
    @return: JSON response containing the list of context sections or an error message.
    """
    try:
        get_all = request.args.get('getAll', 'false').lower() == 'true'
        
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        
        if get_all:
            cursor.execute("SELECT id, contextHeader, active, contextText FROM context")
        else:
            cursor.execute("SELECT id, contextHeader, active, contextText FROM context WHERE active = 1")
        
        context_sections = cursor.fetchall()
        conn.close()

        context_list = [
            {"id": row[0], "contextHeader": row[1], "active": row[2], "contextText": row[3]}
            for row in context_sections
        ]

        return jsonify({"contextSections": context_list}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/removeContextSection', methods=['DELETE'])
def removeContextSection():
    """
    API endpoint to remove a context section from the database.
    
    @param request: Flask request object containing JSON payload with the context section ID.
    @return: JSON response indicating success or error message.
    """
    try:
        data = request.get_json()
        context_id = data.get("id", None)
        
        if context_id is None:
            return jsonify({"error": "No context ID provided"}), 400

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("DELETE FROM context WHERE id = ?", (context_id,))
        conn.commit()
        conn.close()

        return jsonify({"message": "Context section removed successfully"}), 200
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

        # Include the active context sections from the database
        response = getContextSections()
        if response.status_code == 200:
            context_sections = response.get_json().get("contextSections", [])
            for section in context_sections:
                if section["active"]:
                    user_input.append({"role": "system", "content": section["contextText"]})
        
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

@app.route('/getAvailableModels', methods=['GET'])
def getAvailableModels():
    """
    API endpoint to get a list of all available models from the OpenAI API.
    
    @return: JSON response containing the list of available models or an error message.
    """
    try:
        models = client.models.list()
        models_list = [model.id for model in models.data]
        return jsonify({"models": models_list}), 200
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
        to_email = data.get("email", "")
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
