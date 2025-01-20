"""
This file contains the backend APIs for MannyConnect.
The APIs are used to connect and use OpenAI's API services and access/store data.

Interacts heavily with the API.js file in the /var/www/html directory to provide
the frontend with the necessary data and interact with the SQLite database.

Date Last Edited: 21/1/2025
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

# Constants for HTTP status codes
SUCCESS_CODE = 200
ERROR_CODE = 500
NOTFOUND_CODE = 404
BADREQUEST_CODE = 400


app = Flask(__name__) # Used for initializing the Flask app.

"""
/ / / / / / / / / / / / / / / / / / / / / / / / 
** Below are the helper methods for the APIs **
Author: Ethan Armbruster, Zach Clouse
\ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ 
"""

"""
Helper function to get the current model from the database.

@RETURNS: The current model as a string, or None if not found.
"""
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

"""
Helper function to check if a context section exists in the database.

@PARAM: contextID - The ID of the context section to check.
@RETURNS: True if the context section exists, False otherwise.
"""
def contextExists(contextID):
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("SELECT id FROM context WHERE id = ?", (contextID,))
        context = cursor.fetchone()
        conn.close()
        return context is not None
    except Exception as e:
        print(f"Error checking context existence: {e}")
        return False

"""
Helper function to get the API key from the database.

@RETURNS: The API key as a string, or None if not found.
"""
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

"""
Helper function to log the chat GPT call to the database.

@PARAM: userMessage - The user's message.
@PARAM: completion - The completion response from the OpenAI API.
"""
def dbLog(userMessage, completion):
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        timestamp = datetime.now().isoformat()
        response_json = json.dumps(completion, default=str)
        cursor.execute("INSERT INTO history (input, output, timestamp) VALUES (?, ?, ?)",
                       (userMessage, response_json, timestamp))
        conn.commit()
        conn.close()
        print("Data logged successfully")
    except Exception as e:
        print(f"Error logging chat GPT call: {e}")
        traceback.print_exc()

"""
Helper function to get the email user from the database.

@RETURNS: The email user as a string, or None if not found.
"""
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

"""
Helper function to get the email password from the database.

@RETURNS: The email password as a string, or None if not found.
"""
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

"""
Helper function to set the email user in the database.

@PARAM: emailUser - The new email user to set.
"""
def setEmailUser(emailUser):
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET emailUser = ? WHERE id = 1", (emailUser,))
        conn.commit()
        conn.close()
    except Exception as e:
        print(f"Error setting email user: {e}")
        traceback.print_exc()

"""
Helper function to set the email password in the database.

@PARAM: emailPass - The new email password to set.
"""
def setEmailPass(emailPass):
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET emailPass = ? WHERE id = 1", (emailPass,))
        conn.commit()
        conn.close()
    except Exception as e:
        print(f"Error setting email password: {e}")
        traceback.print_exc()

client = OpenAI(
    api_key=getAPIKey()
)

"""
Helper function to hash the API key.

@PARAM: api_key - The original API key.
@RETURNS: The hashed API key.
"""
def hash_api_key(api_key):
    if api_key.startswith("sk-proj") and len(api_key) > 10:
        return "sk-proj" + "*" * (len(api_key) - 10) + api_key[-4:]
    return "*" * len(api_key)



"""
/ / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
** Below are the Flask API routes for backend management of the MannyConnect system. **
Author: Ethan Armbruster
\ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ 
"""

"""
API Function to get current model of AI being used.

@ACCEPTS: GET request
@ACCESSED VIA: /getModel
@RETURNS: JSON object with model key and value of model name

Example Return:
{
  "model": "gpt-4o-mini"
}
"""
@app.route('/getModel', methods=['GET'])
def getModelRoute():
    try:
        model = getModel()
        if model:
            return jsonify({"model": model}), SUCCESS_CODE
        else:
            return jsonify({"error": "Model not found"}), NOTFOUND_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to get the current API key.

@ACCEPTS: GET request
@ACCESSED VIA: /getAPIKey
@RETURNS: JSON object with apiKey key and value of hashed API key

Example Return:
{
  "apiKey": "sk-proj************abcd"
}
"""
@app.route('/getAPIKey', methods=['GET'])
def getAPIKeyRoute():
    try:
        api_key = getAPIKey()
        if (api_key):
            hashed_api_key = hash_api_key(api_key)
            return jsonify({"apiKey": hashed_api_key}), SUCCESS_CODE
        else:
            return jsonify({"error": "API key not found"}), NOTFOUND_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to update the model in the database.

@ACCEPTS: POST request
@ACCESSED VIA: /updateModel
@PARAM: JSON object with model key and value of new model name
@RETURNS: JSON object with message key and value indicating success or error

Example Return:
{
  "message": "Model updated successfully"
}
"""
@app.route('/updateModel', methods=['POST'])
def update_model():
    try:
        data = request.get_json()
        new_model = data.get("model", "")
        
        if not new_model:
            return jsonify({"error": "No model provided"}), BADREQUEST_CODE

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET model = ? WHERE id = 1", (new_model,))
        conn.commit()
        conn.close()

        return jsonify({"message": "Model updated successfully"}), SUCCESS_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to update the API key in the database.

@ACCEPTS: POST request
@ACCESSED VIA: /updateAPIKey
@PARAM: JSON object with apiKey key and value of new API key
@RETURNS: JSON object with message key and value indicating success or error

Example Return:
{
  "message": "API key updated successfully"
}
"""
@app.route('/updateAPIKey', methods=['POST'])
def update_api_key():
    try:
        data = request.get_json()
        new_api_key = data.get("apiKey", "")
        
        if not new_api_key:
            return jsonify({"error": "No API key provided"}), BADREQUEST_CODE

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("UPDATE ai SET key = ? WHERE id = 1", (new_api_key,))
        conn.commit()
        conn.close()

        return jsonify({"message": "API key updated successfully"}), SUCCESS_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to upsert a context section in the database.

@ACCEPTS: POST request
@ACCESSED VIA: /upsertContextSection
@PARAM: JSON object with id, contextHeader, active, and contextText keys
@RETURNS: JSON object with message key and value indicating success or error

Example Return:
{
  "message": "Context section upserted successfully"
}
"""
@app.route('/upsertContextSection', methods=['POST'])
def upsertContextSection():
    try:
        data = request.get_json()
        context_id = data.get("id", None)
        context_header = data.get("contextHeader", "")
        active = data.get("active", False)
        context_text = data.get("contextText", "")
        
        if not context_header or not context_text:
            return jsonify({"error": "Missing contextHeader or contextText"}), BADREQUEST_CODE

        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()

        if context_id:
            cursor.execute("""
                UPDATE context
                SET contextHeader = ?, active = ?, contextText = ?
                WHERE id = ?
            """, (context_header, active, context_text, context_id))
        else:
            cursor.execute("""
                INSERT INTO context (contextHeader, active, contextText)
                VALUES (?, ?, ?)
            """, (context_header, active, context_text))
            context_id = cursor.lastrowid

        conn.commit()

    except Exception as e:
        print(f"Error: {e}")
        return jsonify({"error": str(e)}), ERROR_CODE
    finally:
        conn.close()

    return jsonify({"message": "Context section upserted successfully"}), SUCCESS_CODE

"""
API Function to get context sections from the database.

@ACCEPTS: GET request
@ACCESSED VIA: /getContextSections
@RETURNS: JSON object with contextSections key containing a list of context sections and concatenatedContext key containing concatenated context text

Example Return:
{
  "contextSections": [
    {"id": 1, "contextHeader": "header1", "active": true, "contextText": "text1"},
    {"id": 2, "contextHeader": "header2", "active": false, "contextText": "text2"}
  ],
  "concatenatedContext": "text1"
}
"""
@app.route('/getContextSections', methods=['GET'])
def getContextSections():
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()

        get_all = request.args.get('getAll', 'false').lower() == 'true'
        
        if get_all:
            cursor.execute("SELECT id, contextHeader, active, contextText FROM context")
        else:
            cursor.execute("SELECT id, contextHeader, active, contextText FROM context WHERE active = 1")

        context_sections = cursor.fetchall()
        conn.close()

        concatenated_context = ""

        context_list = [
            {"id": row[0], "contextHeader": row[1], "active": row[2], "contextText": row[3]}
            for row in context_sections
        ]

        for section in context_list:
            if section['active']:
                concatenated_context += section['contextText'] + " "

        return jsonify({
            "contextSections": context_list,
            "concatenatedContext": concatenated_context.strip()
        }), SUCCESS_CODE

    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to remove a context section from the database.

@ACCEPTS: DELETE request
@ACCESSED VIA: /removeContextSection
@PARAM: JSON object with id key
@RETURNS: JSON object with message key and value indicating success or error

Example Return:
{
  "message": "Context section removed successfully"
}
"""
@app.route('/removeContextSection', methods=['DELETE'])
def removeContextSection():
    data = request.get_json()
    context_id = data.get("id", None)
        
    if context_id is None:
        return jsonify({"error": "No context ID provided"}), BADREQUEST_CODE

    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        cursor.execute("DELETE FROM context WHERE id = ?", (context_id,))
        conn.commit()
        conn.close()

        return jsonify({"message": "Context section removed successfully"}), SUCCESS_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to handle user messages and return AI responses.

@ACCEPTS: POST request
@ACCESSED VIA: /chatbot
@PARAM: JSON object with user_message key
@RETURNS: AI response as plain text

Example Return:
"Hello! How can I assist you today?"
"""
@app.route('/chatbot', methods=['POST'])
def chatbot():
    database = "database.db"
    try:
        data = request.get_json()
        user_message = data.get("user_message", "")
        
        if not user_message:
            return jsonify({"error": "No user message provided"}), BADREQUEST_CODE

        user_input = []

        conn = sqlite3.connect(database)
        cursor = conn.cursor()
        cursor.execute("SELECT contextText FROM context WHERE active = 1")
        context_sections = cursor.fetchall()
        conn.close()

        for section in context_sections:
            user_input.append({"role": "system", "content": section[0]})
        
        user_input.append({"role": "user", "content": user_message})

        completion = client.chat.completions.create(
            model=getModel(),
            messages=user_input,
        )

        ai_response = completion.choices[0].message.content
        
        dbLog(user_message, completion)
        
        return ai_response
    
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to check if the provided username and password are valid.

@ACCEPTS: POST request
@ACCESSED VIA: /login
@PARAM: JSON object with username and password keys
@RETURNS: JSON object with valid key indicating whether the login is successful

Example Return:
{
  "valid": true
}
"""
@app.route('/login', methods=['POST'])
def login():
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
            return jsonify({"valid": True}), SUCCESS_CODE
        else:
            return jsonify({"valid": False}), SUCCESS_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to get a list of all available models from the OpenAI API.

@ACCEPTS: GET request
@ACCESSED VIA: /getAvailableModels
@RETURNS: JSON object with models key containing a list of available models

Example Return:
{
  "models": ["gpt-3.5-turbo", "gpt-4"]
}
"""
@app.route('/getAvailableModels', methods=['GET'])
def getAvailableModels():
    try:
        models = client.models.list()
        models_list = [model.id for model in models.data]
        return jsonify({"models": models_list}), SUCCESS_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to send an email.

@ACCEPTS: POST request
@ACCESSED VIA: /sendEmail
@PARAM: JSON object with email and message keys
@RETURNS: JSON object with message key indicating success or error

Example Return:
{
  "message": "Email sent successfully"
}
"""
@app.route('/sendEmail', methods=['POST'])
def send_email():
    try:
        data = request.get_json()
        to_email = data.get("email", "")
        message_content = data.get("message", "")
        
        if not to_email or not message_content:
            return jsonify({"error": "Missing email or message"}), BADREQUEST_CODE

        sender_email = getEmailUser()
        sender_password = getEmailPass()

        emailSubject = "Your ManChatBot Chat"
        smtpServer = "smtp.gmail.com"
        smtpPort = 587
        msg = MIMEMultipart()
        msg['From'] = sender_email
        msg['To'] = to_email
        msg['Subject'] = emailSubject
        msg.attach(MIMEText(message_content, 'plain'))

        server = smtplib.SMTP(smtpServer, smtpPort)
        server.starttls()
        server.login(sender_email, sender_password)
        server.sendmail(sender_email, to_email, msg.as_string())
        server.quit()

        return jsonify({"message": "Email sent successfully"}), SUCCESS_CODE
    except Exception as e:
        print(f"Error sending email: {e}")
        traceback.print_exc()
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to get the email user from the database.

@ACCEPTS: GET request
@ACCESSED VIA: /getUser
@RETURNS: JSON object with email_user key containing the email user

Example Return:
{
  "email_user": "user@example.com"
}
"""
@app.route('/getUser', methods=['GET'])
def get_user_route():
    try:
        email_user = getEmailUser()
        if email_user:
            return jsonify({"email_user": email_user}), SUCCESS_CODE
        else:
            return jsonify({"error": "Email user not found"}), NOTFOUND_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to get the email password from the database.

@ACCEPTS: GET request
@ACCESSED VIA: /getPass
@RETURNS: JSON object with email_pass key containing the email password

Example Return:
{
  "email_pass": "password123"
}
"""
@app.route('/getPass', methods=['GET'])
def get_pass_route():
    try:
        email_pass = getEmailPass()
        if email_pass:
            return jsonify({"email_pass": email_pass}), SUCCESS_CODE
        else:
            return jsonify({"error": "Email password not found"}), NOTFOUND_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to get the email user from the database.

@ACCEPTS: GET request
@ACCESSED VIA: /getEmailUser
@RETURNS: JSON object with email_user key containing the email user

Example Return:
{
  "email_user": "user@example.com"
}
"""
@app.route('/getEmailUser', methods=['GET'])
def get_email_user_route():
    try:
        email_user = getEmailUser()
        if email_user:
            return jsonify({"email_user": email_user}), SUCCESS_CODE
        else:
            return jsonify({"error": "Email user not found"}), NOTFOUND_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

"""
API Function to get the email password from the database.

@ACCEPTS: GET request
@ACCESSED VIA: /getEmailPass
@RETURNS: JSON object with email_pass key containing the email password

Example Return:
{
  "email_pass": "password123"
}
"""
@app.route('/getEmailPass', methods=['GET'])
def get_email_pass_route():
    try:
        email_pass = getEmailPass()
        if email_pass:
            return jsonify({"email_pass": email_pass}), SUCCESS_CODE
        else:
            return jsonify({"error": "Email password not found"}), NOTFOUND_CODE
    except Exception as e:
        return jsonify({"error": str(e)}), ERROR_CODE

# Code to run Flask server
if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)
