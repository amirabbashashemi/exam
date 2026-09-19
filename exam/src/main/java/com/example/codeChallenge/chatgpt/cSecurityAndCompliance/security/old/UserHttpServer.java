package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.security.old;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class UserHttpServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/users", new UserHandler());
        server.start();
    }
}
