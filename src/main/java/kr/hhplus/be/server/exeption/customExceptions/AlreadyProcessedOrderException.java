package kr.hhplus.be.server.exeption.customExceptions;

public class AlreadyProcessedOrderException extends RuntimeException{
    public AlreadyProcessedOrderException(String message) {
        super(message);
    }
}
