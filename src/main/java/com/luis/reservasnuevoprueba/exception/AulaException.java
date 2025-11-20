package com.luis.reservasnuevoprueba.exception;

public class AulaException extends RuntimeException {
    public AulaException(String mensaje) {
        super(mensaje);
    }

    public AulaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}