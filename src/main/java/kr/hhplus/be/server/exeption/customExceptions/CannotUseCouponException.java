package kr.hhplus.be.server.exeption.customExceptions;

public class CannotUseCouponException extends RuntimeException{
    public CannotUseCouponException(String message) {
        super(message);
    }
}
