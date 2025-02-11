package kr.hhplus.be.server.exeption.customExceptions;

public class InvalidPaymentException extends RuntimeException{
    public InvalidPaymentException(String message) {
        super(message);
    }
}
