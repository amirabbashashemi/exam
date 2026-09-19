package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.scalability;

import com.sun.net.httpserver.Request;

public class RequestServiceOld {

    public void handle(Request request) {
        process(request);
    }

    private void process(Request request) {
        // heavy CPU + I/O work
    }

}
