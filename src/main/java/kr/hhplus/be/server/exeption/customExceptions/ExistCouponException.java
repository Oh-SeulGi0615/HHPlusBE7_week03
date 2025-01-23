package kr.hhplus.be.server.exeption.customExceptions;

public class ExistCouponException extends RuntimeException{
    public ExistCouponException(String message) {
        super(message);
    }
}
