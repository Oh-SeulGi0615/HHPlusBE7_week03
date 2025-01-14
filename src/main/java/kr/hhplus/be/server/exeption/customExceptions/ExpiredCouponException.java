package kr.hhplus.be.server.exeption.customExceptions;

public class ExpiredCouponException extends RuntimeException{
    public ExpiredCouponException(String message) {
        super(message);
    }
}
