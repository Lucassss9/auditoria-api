package br.com.auditoria.exception;

public class CfIndisponivelException extends RuntimeException {

    public CfIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
