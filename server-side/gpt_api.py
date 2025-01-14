"""
Author: Ethan Armbruster, Zach Clouse, Jalen Brunson
Contact Email: armbrue2@miamioh.edu
"""

from flask import Flask, request, jsonify
from openai import OpenAI
from dotenv import load_dotenv
import os
import sqlite3

# Load environment variables from .env file
load_dotenv()

# Initialize the OpenAI client
client = OpenAI(
    api_key=os.getenv("OPENAI_API_KEY")
)

print("API Key:", os.getenv("OPENAI_API_KEY"))

# Initialize Flask app
app = Flask(__name__)

# Read context information from context.txt
with open('context.txt', 'r') as file:
    context_info = file.read()


"""
API endpoint to handle user messages and return AI responses.
    
@param request: Flask request object containing JSON payload with user message.
@return: JSON response containing AI response or error message.
"""

@app.route('/chatbot', methods=['POST'])
def chatbot():
    try:
        # Get the JSON payload from the request
        data = request.get_json()
        user_message = data.get("user_message", "")
        
        if not user_message:
            return jsonify({"error": "No user message provided"}), 400

        user_input = []
        try:
            # Read conversation history from history.txt
            with open('history.txt', 'r') as history_file:
                buffer = []  # Temporary storage for grouping lines of a single message
                current_role = None  # Tracks the role of the current message

                for line in history_file:
                    line = line.strip()

                    # Skip empty lines
                    if not line:
                        continue

                    # Detect new messages by the `role: content` format
                    if ': ' in line:
                        # Save the previous message if present
                        if current_role and buffer:
                            user_input.append({"role": current_role, "content": "\n".join(buffer)})
                        
                        # Start a new message
                        current_role, content = line.split(': ', 1)
                        if current_role not in ["system", "user", "assistant"]:
                            current_role = None  # Reset role for invalid entries
                            buffer = []
                            continue
                        
                        buffer = [content.replace("\\n", "\n")]  # Unescape newlines
                    else:
                        # Append additional lines to the current message buffer
                        buffer.append(line.replace("\\n", "\n"))

                # Add the last message if present
                if current_role and buffer:
                    user_input.append({"role": current_role, "content": "\n".join(buffer)})

        except FileNotFoundError:
            # Add the context information to the user input if history.txt does not exist
            user_input.append({"role": "system", "content": context_info})
        
        # Add the user's message to the user input
        user_input.append({"role": "user", "content": user_message})

        # Call the OpenAI API
        completion = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=user_input
        )

        # Extract the AI response
        ai_response = completion.choices[0].message.content
        
        # Add the AI's response to the user input
        user_input.append({"role": "assistant", "content": ai_response})
        
        # Save the updated user input to history.txt
        with open('history.txt', 'a') as history_file:
            for message in user_input[-2:]:
                role = message["role"]
                content = message["content"].replace("\n", "\\n")  # Escape newlines for safer storage
                history_file.write(f"{role}: {content}\n")
        
        # Return the AI response
        return jsonify({"ai_response": ai_response})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/register', methods=['POST'])
def register():
    """
    API endpoint to register a new user in the SQLite database.
    
    @param request: Flask request object containing JSON payload with username, password, and email.
    @return: JSON response indicating success or error message.
    """
    try:
        # Get the JSON payload from the request
        data = request.get_json()
        username = data.get("username", "")
        password = data.get("password", "")
        email = data.get("email", "")
        
        if not username or not password or not email:
            return jsonify({"error": "Missing username, password, or email"}), 400

        # Connect to the SQLite database
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()

        # Insert the new user into the account table
        cursor.execute("""
            INSERT INTO account (username, password, email)
            VALUES (?, ?, ?)
        """, (username, password, email))

        # Commit the transaction and close the connection
        conn.commit()
        conn.close()

        return jsonify({"message": "User registered successfully"}), 201
    
    except sqlite3.IntegrityError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)