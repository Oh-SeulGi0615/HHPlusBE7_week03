package kr.hhplus.be.server.exeption;

public class InvalidPaymentException extends RuntimeException{
    public InvalidPaymentException(String message) {
        super(message);
    }
}
