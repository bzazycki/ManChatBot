from flask import Flask, request, jsonify
from openai import OpenAI

# Initialize the OpenAI client
client = OpenAI(
    api_key="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"
)

# Initialize Flask app
app = Flask(__name__)

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

        # Call the OpenAI API
        completion = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "user", "content": user_message}
            ]
        )

        
        # Extract the AI response
        ai_response = completion.choices[0].message.content
        
        # Return the AI response
        return jsonify({"ai_response": ai_response})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Run the Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)