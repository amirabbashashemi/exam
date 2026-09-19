package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.security.neww;

import com.sun.net.httpserver.HttpServer;
import codeChallenge.chatgpt.cSecurityAndCompliance.security.old.UserHandler;

import java.net.InetSocketAddress;

public class UserHttpServer {

    static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/users", new UserHandler());
        server.start();
    }

}
