package de.alexandermora.erplite.domain.exception;

public class MyBusinessException extends RuntimeException {

    public MyBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
