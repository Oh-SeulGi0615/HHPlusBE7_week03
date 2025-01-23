package kr.hhplus.be.server.exeption.customExceptions;

public class InvalidCouponException extends RuntimeException{
    public InvalidCouponException(String message) {
        super(message);
    }
}
