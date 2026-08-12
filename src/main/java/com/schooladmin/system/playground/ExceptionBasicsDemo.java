package com.schooladmin.system.playground;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Standalone demo (no Spring) of checked vs. unchecked exceptions, {@code finally}, and
 * try-with-resources. Run directly:
 *
 * <pre>{@code
 * java -cp target/classes com.schooladmin.system.playground.ExceptionBasicsDemo
 * }</pre>
 */
public class ExceptionBasicsDemo {

    public static void main(String[] args) {
        demonstrateUnchecked();
        demonstrateCheckedHandled();
        demonstrateTryWithResources();
    }

    private static void demonstrateUnchecked() {
        try {
            int result = 10 / 0;
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Caught unchecked exception: " + e.getMessage());
        } finally {
            System.out.println("finally always runs (unchecked demo)");
        }
    }

    private static void demonstrateCheckedHandled() {
        try {
            readFirstLine("does-not-exist.txt");
        } catch (IOException e) {
            System.out.println("Caught checked exception: " + e.getMessage());
        }
    }

    private static String readFirstLine(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.readLine();
        }
    }

    private static void demonstrateTryWithResources() {
        try (AutoCloseable resource = () -> System.out.println("resource closed automatically")) {
            System.out.println("using resource");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
