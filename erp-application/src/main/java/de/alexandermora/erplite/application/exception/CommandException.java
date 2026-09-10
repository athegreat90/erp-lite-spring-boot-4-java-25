package de.alexandermora.erplite.application.exception;

public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
