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
            // ArithmeticException extends RuntimeException -> unchecked. Nothing above this
            // method (not this method's signature, not main()) is required to mention it;
            // the code below would compile identically with the try/catch removed entirely.
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
            // readFirstLine declares "throws IOException" (checked), so the compiler
            // requires every caller to either catch it, like here, or declare "throws
            // IOException" themselves and pass the obligation further up.
            readFirstLine("does-not-exist.txt");
        } catch (IOException e) {
            System.out.println("Caught checked exception: " + e.getMessage());
        }
    }

    private static String readFirstLine(String path) throws IOException {
        // FileReader's constructor can throw FileNotFoundException, which extends
        // IOException -> checked. Remove "throws IOException" above and this file stops
        // compiling entirely; try it.
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.readLine();
        }
    }

    private static void demonstrateTryWithResources() {
        // Caught as the broad "Exception" here (not something narrower) because
        // AutoCloseable.close() is itself declared "throws Exception" in the JDK -- there
        // is no more specific checked type to catch against for an arbitrary AutoCloseable.
        try (AutoCloseable resource = () -> System.out.println("resource closed automatically")) {
            System.out.println("using resource");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
