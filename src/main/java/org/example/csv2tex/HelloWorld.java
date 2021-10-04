package org.example.csv2tex;

import java.io.PrintStream;

public class HelloWorld {
    public static void main(String[] args) {
        printHelloWorld(System.out);
    }

    public static void printHelloWorld(PrintStream outputStream) {
        outputStream.println("Hello, world!");
    }
}
