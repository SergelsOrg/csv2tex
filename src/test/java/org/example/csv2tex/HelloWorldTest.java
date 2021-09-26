package org.example.csv2tex;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloWorldTest {

    @Test
    public void printHelloWorld() {
        // arrange
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        // act
        HelloWorld.printHelloWorld(printStream);

        // assert
        String actualOutput = out.toString();
        assertThat(actualOutput).isEqualTo("Hello, world!\n");
    }
}
