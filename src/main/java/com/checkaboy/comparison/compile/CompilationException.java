package com.checkaboy.comparison.compile;

/**
 * Exception thrown during compilation.
 *
 * @author Taras Shaptala
 */
public class CompilationException
        extends RuntimeException {

    private final String stage;
    private final String code;

    public CompilationException(String message) {
        super(message);
        this.stage = "UNKNOWN";
        this.code = "COMPILATION_ERROR";
    }

    public CompilationException(String message, String stage, String code) {
        super(message);
        this.stage = stage;
        this.code = code;
    }

    public CompilationException(String message, String stage, String code, Throwable cause) {
        super(message, cause);
        this.stage = stage;
        this.code = code;
    }

    public String getStage() {
        return stage;
    }

    public String getCode() {
        return code;
    }

}