package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.interoperability.neww;

import codeChallenge.chatgpt.eCommon.User;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExternalUserService {

    public void sendUser(User user) {
        try {
            String json = "{ \"name\": \"" + user.getName() + "\", \"age\": " + user.getAge() + " }";

            URL url = new URL("http://external-system/api/user");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(json.getBytes());
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

