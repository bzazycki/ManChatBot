from flask import Flask, request, jsonify
from openai import OpenAI
import os

# Initialize the OpenAI client
client = OpenAI(
    api_key="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"
)

# Initialize Flask app
app = Flask(__name__)

# Path to the keywords file
KEYWORDS_FILE = "keywords.txt"

def load_keywords(file_path):
    """
    Loads keywords and their associated responses from a .txt file.
    Returns a dictionary where keys are keywords and values are responses.
    """
    keywords = {}
    if os.path.exists(file_path):
        with open(file_path, "r", encoding="utf-8") as file:
            for line in file:
                if ":" in line:
                    keyword, response = line.strip().split(":", 1)
                    keywords[keyword.strip().lower()] = response.strip()
    return keywords

def find_keyword_response(keywords, user_message):
    """
    Searches for keywords in the user's message and returns the corresponding response.
    If no keywords are found, returns a default message.
    """
    for keyword, response in keywords.items():
        if keyword in user_message.lower():
            return response
    return "Sorry, I couldn't find relevant information for your request."

@app.route('/chatbot', methods=['POST'])
def chatbot():
    """
    API endpoint to handle user messages and return AI responses.
    """
    try:
        # Get the JSON payload from the request
        data = request.get_json()
        user_message = data.get("user_message", "")
        
        if not user_message:
            return jsonify({"error": "No user message provided"}), 400

        # Attempt to call the OpenAI API
        completion = client.chat.completions.create(
            model="gpt-4o-mini",
            store=True,
            messages=[
                {"role": "user", "content": user_message}
            ]
        )
        
        # Extract the AI response
        ai_response = completion.choices[0].message.content
        
        # Return the AI response
        return jsonify({"ai_response": ai_response})
    
    except Exception:
        # Fallback mechanism: Load keywords and search for a response
        keywords = load_keywords(KEYWORDS_FILE)
        fallback_response = find_keyword_response(keywords, user_message)
        return jsonify({"ai_response": fallback_response})

# Run the Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
