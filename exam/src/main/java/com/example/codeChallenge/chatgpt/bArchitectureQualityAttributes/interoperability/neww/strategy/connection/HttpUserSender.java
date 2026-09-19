package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.interoperability.neww.strategy.connection;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUserSender implements UserSender {

    @Override
    public void send(String message) {
        try {

            URL url = new URL("http://external-system/api/user");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(message.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed to send user");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error sending user", e);
        }
    }
}

