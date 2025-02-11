package kr.hhplus.be.server.exeption.customExceptions;

public class ExceedMaxChargeAmountException extends RuntimeException{
    public ExceedMaxChargeAmountException(String message) {
        super(message);
    }
}
