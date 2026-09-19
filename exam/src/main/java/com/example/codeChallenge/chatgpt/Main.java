package com.example.codeChallenge.chatgpt;

import codeChallenge.chatgpt.aCoreSystemDesignAttributes.reliability.FileProcessor;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws URISyntaxException {


        FileProcessor fileProcessor = new FileProcessor();
        fileProcessor.processFiles(List.of(Path.of(new URI(""))));


    }
}