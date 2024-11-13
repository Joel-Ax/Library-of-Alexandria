package com.example.libraryofalexandria.Exceptions;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private HttpStatus status;
  private String message;
  private LocalDateTime timestamp = LocalDateTime.now();

  public ErrorResponse(HttpStatus httpStatus, String message) {
  }
}
