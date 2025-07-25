package br.com.danielwisky.rinhadebackend.gateways.inputs.http;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.response.ErrorResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public HttpEntity<ErrorResponse> handlerMethodArgumentNotValidException(
      final MethodArgumentNotValidException ex) {
    log.debug(ex.getMessage(), ex);
    final var bindingResult = ex.getBindingResult();
    final var fieldErrors = bindingResult.getFieldErrors();
    final var message = this.processFieldErrors(fieldErrors);
    return new ResponseEntity<>(message, buildHttpHeader(), BAD_REQUEST);
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public HttpEntity<ErrorResponse> handle400Exceptions(final RuntimeException ex) {
    log.debug(ex.getMessage(), ex);
    return new ResponseEntity<>(createResponse(ex), buildHttpHeader(), BAD_REQUEST);
  }

  @ExceptionHandler(Throwable.class)
  public HttpEntity<ErrorResponse> handleThrowable(final Throwable ex) {
    log.error(ex.getMessage(), ex);
    return new ResponseEntity<>(createResponse(ex), buildHttpHeader(), INTERNAL_SERVER_ERROR);
  }

  private HttpHeaders buildHttpHeader() {
    final var responseHeaders = new HttpHeaders();
    responseHeaders.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    return responseHeaders;
  }

  private ErrorResponse createResponse(final Throwable ex) {
    return createResponse(ex.getMessage());
  }

  private ErrorResponse createResponse(final String message) {
    return isNoneBlank(message) ? new ErrorResponse(List.of(message)) : null;
  }

  private ErrorResponse processFieldErrors(final List<FieldError> fieldErrors) {
    final var errors = fieldErrors
        .stream()
        .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
        .toList();
    return new ErrorResponse(errors);
  }
}