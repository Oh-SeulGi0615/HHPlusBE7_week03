package kr.hhplus.be.server.exeption;

public class InvalidCouponException extends RuntimeException{
    public InvalidCouponException(String message) {
        super(message);
    }
}
