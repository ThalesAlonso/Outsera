package com.outsera.razzies.exception;

public class CsvLoadingException extends RuntimeException {

    public CsvLoadingException(String mensagem) {
        super(mensagem);
    }

    public CsvLoadingException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
