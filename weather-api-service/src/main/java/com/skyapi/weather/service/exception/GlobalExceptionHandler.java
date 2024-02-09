package com.skyapi.weather.service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDTO handlerGenericException(HttpServletRequest request, Exception ex) {
    ErrorDTO error = ErrorDTO.builder()
          .timeStamp(new Date())
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .errors(List.of(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
          .path(request.getServletPath())
          .build();

    log.error(ex.getMessage(), ex);

    return error;
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO handlerBadRequestException(HttpServletRequest request, Exception ex) {
    ErrorDTO error = ErrorDTO.builder()
          .timeStamp(new Date())
          .status(HttpStatus.BAD_REQUEST.value())
          .errors(List.of(ex.getMessage()))
          .path(request.getServletPath())
          .build();

    log.error(ex.getMessage(), ex);

    return error;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO handleConstraintViolationException(HttpServletRequest request, Exception ex) {

    return ErrorDTO.builder()
          .timeStamp(new Date())
          .status(HttpStatus.BAD_REQUEST.value())
          .errors(List.of(ex.getMessage()))
          .path(request.getServletPath())
          .build();
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        HttpStatusCode status,
        @NonNull WebRequest request
  ) {

    log.error(ex.getMessage(), ex);

    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

    List<String> errors = fieldErrors.stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage)
          .toList();

    ErrorDTO error = ErrorDTO.builder()
          .timeStamp(new Date())
          .status(status.value())
          .errors(errors)
          .path(((ServletWebRequest) request).getRequest().getServletPath())
          .build();

    return new ResponseEntity<>(error, headers, status);
  }
}
