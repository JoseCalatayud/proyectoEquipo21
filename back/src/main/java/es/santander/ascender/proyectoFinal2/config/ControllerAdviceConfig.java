package es.santander.ascender.proyectoFinal2.config;

import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import es.santander.ascender.proyectoFinal2.exception.StockInsuficienteException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdviceConfig {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErronInfo> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErronInfo errorResponse = new ErronInfo(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErronInfo> handleIllegalStateException(IllegalStateException ex) {
        ErronInfo errorResponse = new ErronInfo(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErronInfo> handleStockInsuficienteException(StockInsuficienteException ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("nombreArticulo", ex.getNombreArticulo());
        details.put("stockDisponible", ex.getStockDisponible());
        details.put("cantidadSolicitada", ex.getCantidadSolicitada());
        
        ErronInfo errorResponse = new ErronInfo(ex.getMessage(), details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErronInfo> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = "Error de validación";
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage(),
                (errorMsg1, errorMsg2) -> errorMsg1 + ", " + errorMsg2
            ));
            
        ErronInfo errorResponse = new ErronInfo(message, errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErronInfo> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                violation -> violation.getMessage(),
                (errorMsg1, errorMsg2) -> errorMsg1 + ", " + errorMsg2
            ));
            
        ErronInfo errorResponse = new ErronInfo("Error de validación", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErronInfo> handleBadCredentialsException(BadCredentialsException ex) {
        ErronInfo errorResponse = new ErronInfo("Credenciales inválidas");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErronInfo> handleAccessDeniedException(AccessDeniedException ex) {
        ErronInfo errorResponse = new ErronInfo("No tiene permisos para realizar esta acción");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErronInfo> handleException(Exception ex) {
        ErronInfo errorResponse = new ErronInfo("Se ha producido un error inesperado");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErronInfo> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
        ErronInfo errorResponse = new ErronInfo("Error de concurrencia al actualizar el stock. Por favor, intente nuevamente.");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}