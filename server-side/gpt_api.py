"""
Author: Ethan Armbruster, Zach Clouse, Jalen Brunson
Contact Email: armbrue2@miamioh.edu
"""

from flask import Flask, request, jsonify
from openai import OpenAI
from dotenv import load_dotenv
import os

# Load environment variables from .env file
load_dotenv()

# Initialize the OpenAI client
client = OpenAI(
    api_key=os.getenv("OPENAI_API_KEY")
)

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
    
"""
Request format:
        {
            "user_message": "string"
        }
    
Response format:
        {
            "ai_response": "string"
        }
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
                for line in history_file:
                    role, content = line.strip().split(': ', 1)
                    user_input.append({"role": role, "content": content})
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
                history_file.write(f"{message['role']}: {message['content']}\n")
        
        # Return the AI response
        return jsonify({"ai_response": ai_response})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)