package com.rafaelsousa.algashop.product.catalog.presentation;

import com.rafaelsousa.algashop.product.catalog.application.product.ResourceNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.DomainEntityNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.DomainException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String TIMESTAMP_PROPERTY_NAME = "timestamp";

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
              HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Invalid fields");
        problemDetail.setDetail("One or more fields are invalid");
        problemDetail.setType(URI.create("/errors/invalid-fields"));

        Map<String, String> fieldErrors = ex.getBindingResult().getAllErrors().stream().collect(
                Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        objectError -> messageSource.getMessage(objectError, LocaleContextHolder.getLocale())
                )
        );

        problemDetail.setProperty("fields", fieldErrors);
        problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

  @ExceptionHandler({DomainEntityNotFoundException.class, ResourceNotFoundException.class})
  public ProblemDetail handleResourceNotFoundException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("/errors/not-found"));
        problemDetail.setTitle("Not found");
        problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

        return problemDetail;
    }

  @ExceptionHandler({DomainException.class, UnprocessableContentException.class})
  public ProblemDetail handleUnprocessableContentException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problemDetail.setType(URI.create("/errors/unprocessable-content"));
        problemDetail.setTitle("Unprocessable content");
        problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

        return problemDetail;
    }
}