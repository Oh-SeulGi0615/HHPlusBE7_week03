package kr.hhplus.be.server.exeption.customExceptions;

public class ExceedMaxBalanceException extends RuntimeException{
    public ExceedMaxBalanceException(String message) {
        super(message);
    }
}
