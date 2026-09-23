package de.alexandermora.erplite.controller.exception;

import de.alexandermora.erplite.application.exception.CommandException;
import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.domain.exception.MyBusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
     * HTTP STATUS 409
     */
    @ExceptionHandler(MyBusinessException.class)
    public ProblemDetail handleBusinessException(MyBusinessException ex, HttpServletRequest request) {

        log.warn("Rule MyBusinessException violation detected");

        return buildProblemDetail(
                HttpStatus.CONFLICT,
                "Business rule violation",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /*
     * HTTP STATUS 422
     */
    @ExceptionHandler(CommandException.class)
    public ProblemDetail handleCommandException(CommandException ex, HttpServletRequest request) {

        log.warn("Rule CommandException violation detected");

        return buildProblemDetail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Command rule violation",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /*
     * HTTP STATUS 404 - 500
     */
    @ExceptionHandler(QueryException.class)
    public ProblemDetail handleQueryException(QueryException ex, HttpServletRequest request) {

        log.warn("Rule QueryException violation detected");

        var isInfraFailure = ex.getCause() instanceof RuntimeException;

        if (isInfraFailure) {
            log.error("Rule QueryException by infrastructure detected");
            return get500ProblemDetail(request.getRequestURI());
        }

        return buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "Resource wasn't found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /*
     * HTTP STATUS 500
     */
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handlerRuntimeException(RuntimeException ex, HttpServletRequest request) {

        log.error("General error detected", ex);

        return get500ProblemDetail(request.getRequestURI());
    }

    /*
     * HTTP STATUS 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handlerMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "InvalidValue",
                        (existingValue, newValue) -> newValue
                ));

        var problemDetail = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid request data",
                "One or more fields are invalid",
                request.getRequestURI()
        );
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    private ProblemDetail get500ProblemDetail(String uri) {
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal error occurred",
                "Unexpected error occurred",
                uri
        );
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail, String path) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(path));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
