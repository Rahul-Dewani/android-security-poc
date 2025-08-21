from flask import Flask, request, jsonify
import json

# Create a Flask web server application
app = Flask(__name__)

@app.route('/collect', methods=['POST'])
def collect_data():
    """
    This is our API endpoint. It waits for POST requests.
    """
    print("\n[*] Received a request at /collect endpoint!")
    
    try:
        # Get the JSON data sent from the Android app
        data = request.get_json()
        
        if not data:
            print("[!] Request did not contain JSON data.")
            return jsonify({"status": "error", "message": "No JSON data received"}), 400

        print("\n" + "="*50)
        print("      RECEIVED DATA FROM TARGET")
        print("="*50 + "\n")
        
        # Pretty-print the received JSON
        print(json.dumps(data, indent=4))
        
        print("\n" + "="*50)
        
        # Send a success response back to the app
        return jsonify({"status": "success", "message": "Data received"}), 200

    except Exception as e:
        print(f"[!!!] An error occurred: {e}")
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    # Run the server on all network interfaces on port 5000
    # The default port for Flask is 5000
    app.run(host='0.0.0.0', port=5000, debug=True)
