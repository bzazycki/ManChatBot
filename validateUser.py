from flask import Flask, request, jsonify
import sqlite3
import os

app = Flask(__name__)

DATABASE = 'database.db'

def query_db(query, args=(), one=False):
    con = sqlite3.connect(DATABASE)
    cur = con.cursor()
    cur.execute(query, args)
    rv = cur.fetchall()
    con.close()
    return (rv[0] if rv else None) if one else rv

@app.route('/validateUser', methods=['POST'])
def validate_user():
    data = request.get_json()
    username = data['username']
    password = data['password']
    user = query_db('SELECT * FROM account WHERE username = ? AND password = ?', [username, password], one=True)
    
    if user:
        return jsonify(valid=True)
    else:
        return jsonify(valid=False)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
