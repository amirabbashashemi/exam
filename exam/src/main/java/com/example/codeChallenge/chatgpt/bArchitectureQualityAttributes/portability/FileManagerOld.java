package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.portability;

import java.io.*;

public class FileManagerOld {

    public void saveText(String filename, String content) {
        try {
            String path = "C:\\app\\data\\" + filename;  // مشکل: فقط مسیر ویندوز
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot save file", e);
        }
    }

    public String readText(String filename) {
        try {
            String path = "C:\\app\\data\\" + filename;  // مشکل: فقط مسیر ویندوز
            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file", e);
        }
    }
}
