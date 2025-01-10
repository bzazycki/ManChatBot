from flask import Flask, request, jsonify
from openai import OpenAI

# Initialize the OpenAI client
client = OpenAI(
    api_key="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"
)

# Initialize Flask app
app = Flask(__name__)

# Global variable to store persistent context
persistent_context = ""

# Load context from a .txt file
def load_persistent_context(file_path):
    try:
        with open(file_path, 'r') as file:
            return file.read()
    except FileNotFoundError:
        return ""

# Load persistent context at startup
persistent_context = load_persistent_context("context.txt")

@app.route('/chatbot', methods=['POST'])
def chatbot():
    """
    API endpoint to handle user messages and return AI responses.
    
    Request format:
        {
            "user_message": "string",
            "context_info": "string"
        }
    
    Response format:
        {
            "ai_response": "string"
        }
    """
    try:
        # Get the JSON payload from the request
        data = request.get_json()
        user_message = data.get("user_message", "")
        context_info = data.get("context_info", "")

        if not user_message:
            return jsonify({"error": "No user message provided"}), 400

        # Prepare the conversation history
        user_input = []

        # Include persistent context only if it's the first message
        if not context_info and persistent_context:
            user_input.append({"role": "system", "content": persistent_context})

        # Include any additional context provided by the user
        if context_info:
            user_input.append({"role": "system", "content": context_info})

        # Add the user's message
        user_input.append({"role": "user", "content": user_message})

        # Call the OpenAI API
        completion = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=user_input
        )

        # Extract the AI response
        ai_response = completion.choices[0].message.content

        # Return the AI response
        return jsonify({"ai_response": ai_response})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Run the Flask app
if __name__ == '__main__':
    # Ensure the context.txt file is loaded before starting the server
    app.run(host='0.0.0.0', port=5000, debug=True)
