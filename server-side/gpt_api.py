from flask import Flask, request, jsonify
from openai import OpenAI

# Initialize the OpenAI client
client = OpenAI(
    api_key="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"
)

# Initialize Flask app
app = Flask(__name__)

# Read context information from context.txt
with open('context.txt', 'r') as file:
    context_info = file.read()

@app.route('/chatbot', methods=['POST'])
def chatbot():
    """
    API endpoint to handle user messages and return AI responses.
    
    Request format:
        {
            "user_message": "string"
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
        
        if not user_message:
            return jsonify({"error": "No user message provided"}), 400

        # Read conversation history from history.txt
        conversation_history = []
        try:
            with open('history.txt', 'r') as history_file:
                for line in history_file:
                    role, content = line.strip().split(': ', 1)
                    conversation_history.append({"role": role, "content": content})
        except FileNotFoundError:
            pass
        
        # Add the context information to the conversation history if it's the first message
        if not conversation_history:
            conversation_history.append({"role": "system", "content": context_info})
        
        # Add the user's message to the conversation history
        conversation_history.append({"role": "user", "content": user_message})

        # Call the OpenAI API
        completion = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=conversation_history
        )

        # Extract the AI response
        ai_response = completion.choices[0].message.content
        
        # Add the AI's response to the conversation history
        conversation_history.append({"role": "assistant", "content": ai_response})
        
        # Save the updated conversation history to history.txt
        with open('history.txt', 'a') as history_file:
            for message in conversation_history[-2:]:  # Only append the last user and assistant messages
                history_file.write(f"{message['role']}: {message['content']}\n")
        
        # Return the AI response
        return jsonify({"ai_response": ai_response})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Run the Flask app
if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)