package de.alexandermora.erplite.application.exception;

public class QueryException extends RuntimeException {
    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryException(String message) {
        super(message);
    }
}
