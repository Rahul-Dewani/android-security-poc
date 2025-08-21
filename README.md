# Android Security Proof-of-Concept Projects

This repository contains the source code for two Android proof-of-concept (PoC) applications developed for a cybersecurity interview task. The projects demonstrate a fundamental understanding of attacker techniques in a controlled lab environment to derive defensive insights.

**Disclaimer:** *All code is intended for educational and research purposes only. It should only be run in a controlled, isolated lab environment. Do not deploy or use this code on any production or public-facing systems.*

---

## Task 1: Android Reverse Shell

This project consists of a minimal Android application that initiates a reverse TCP connection to a Python-based Command and Control (C2) server.

### Components
* **Android Client (`/Task-1-Reverse-Shell/android-client/`)**: An Android app that connects to a specified IP and port. It receives shell commands, executes them, and sends the output back.
* **C2 Server (`/Task-1-Reverse-Shell/python-c2-server/`)**: A Python script that listens for incoming connections, allowing the operator to send commands and view output.

### How to Run
1.  Run the `c2_server.py` script on your machine (`python c2_server.py`).
2.  Install and run the Android app in an emulator.
3.  Ensure the IP in the app is `10.0.2.2` (for the emulator) and the port is `4444`.
4.  Click the "Connect" button in the app.
5.  Execute commands from the server's terminal.

### Key Security Insights
* Demonstrates the importance of **egress traffic filtering** on mobile devices.
* Highlights that `Runtime.exec()` is a high-risk function heavily scrutinized by security scanners.
* Shows how even a sandboxed application can provide a dangerous foothold for an attacker.

---

## Task 2: Android Information Stealer

This project simulates an info-stealer that collects device information and exfiltrates it to a collection server via HTTP.

### Components
* **Android Client (`/Task-2-Info-Stealer/android-client/`)**: An Android app that gathers the device model, OS version, installed apps, and mock contacts.
* **Collection Server (`/Task-2-Info-Stealer/python-web-server/`)**: A Python Flask web server that listens for HTTP POST requests and prints the received JSON data.

### How to Run
1.  Install the Flask library (`pip install Flask`).
2.  Run the `collection_server.py` script on your machine (`python collection_server.py`).
3.  Install and run the Android app in an emulator.
4.  Click the "Send Mock Data" button in the app.
5.  Observe the JSON output in the web server's terminal.

### Key Security Insights
* Illustrates **device fingerprinting** as a common reconnaissance technique.
* Shows how attackers use standard protocols like **HTTP** to blend in with normal network traffic and evade simple firewalls.
* Highlights the risk of "dangerous" permissions like `QUERY_ALL_PACKAGES` for user profiling.
