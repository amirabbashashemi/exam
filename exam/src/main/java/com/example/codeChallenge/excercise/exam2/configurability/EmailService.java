package com.example.codeChallenge.excercise.exam2.configurability;

public class EmailService {
    private final String host = System.getenv("host");
    private final int port = Integer.parseInt(System.getenv("port"));
    private final int timeout = Integer.parseInt(System.getenv("timeout"));

    public void send(Email email) {
        // connect(host, port)
        // send email
    }
}