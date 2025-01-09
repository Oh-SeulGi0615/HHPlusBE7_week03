package kr.hhplus.be.server.exeption;

public class ExpiredCouponException extends RuntimeException{
    public ExpiredCouponException(String message) {
        super(message);
    }
}
