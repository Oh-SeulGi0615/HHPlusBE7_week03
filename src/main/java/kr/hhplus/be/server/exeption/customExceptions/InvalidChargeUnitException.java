package kr.hhplus.be.server.exeption.customExceptions;

public class InvalidChargeUnitException extends RuntimeException {
    public InvalidChargeUnitException(String message) {
        super(message);
    }
}
