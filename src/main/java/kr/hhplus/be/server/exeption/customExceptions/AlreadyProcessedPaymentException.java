package kr.hhplus.be.server.exeption.customExceptions;

public class AlreadyProcessedPaymentException extends RuntimeException{
    public AlreadyProcessedPaymentException(String message) {
        super(message);
    }
}
