package br.com.auditoria.exception;

import br.com.auditoria.dto.erro.ErroDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroDto naoEncontrado(NoSuchElementException ex) {
        return new ErroDto("Não encontrado.", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroDto dadosInvalidos(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        if (fieldError != null) {
            return new ErroDto(fieldError.getDefaultMessage(), fieldError.getField());
        }

        return new ErroDto("Dados inválidos.", null);
    }
}
