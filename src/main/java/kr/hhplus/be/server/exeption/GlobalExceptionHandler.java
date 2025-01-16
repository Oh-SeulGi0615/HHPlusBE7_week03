package kr.hhplus.be.server.exeption;

import kr.hhplus.be.server.api.controller.*;
import kr.hhplus.be.server.exeption.customExceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = {RestController.class}, basePackageClasses = {
        UserController.class, PaymentController.class, OrderController.class, GoodsController.class, CouponController.class
})
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserException(InvalidUserException ex) {
        ErrorResponse errorResponse = new ErrorResponse("InvalidUserException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExistUserException.class)
    public ResponseEntity<ErrorResponse> handleExistUserException(ExistUserException ex) {
        ErrorResponse errorResponse = new ErrorResponse("ExistUserException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidPointException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPointException(InvalidPointException ex) {
        ErrorResponse errorResponse = new ErrorResponse("InvalidPointException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentException(InvalidPaymentException ex) {
        ErrorResponse errorResponse = new ErrorResponse("InvalidPaymentException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderException(InvalidOrderException ex) {
        ErrorResponse errorResponse = new ErrorResponse("InvalidOrderException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AlreadyProcessedOrderException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyProcessedOrderException(AlreadyProcessedOrderException ex) {
        ErrorResponse errorResponse = new ErrorResponse("AlreadyProcessedOrderException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AlreadyProcessedPaymentException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyProcessedPaymentException(AlreadyProcessedPaymentException ex) {
        ErrorResponse errorResponse = new ErrorResponse("AlreadyProcessedPaymentException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidGoodsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidGoodsException(InvalidGoodsException ex) {
        ErrorResponse errorResponse = new ErrorResponse("InvalidGoodsException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExistGoodsException.class)
    public ResponseEntity<ErrorResponse> handleExistGoodsException(ExistGoodsException ex) {
        ErrorResponse errorResponse = new ErrorResponse("ExistGoodsException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidCouponException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCouponException(InvalidCouponException ex) {
        ErrorResponse errorResponse = new ErrorResponse("InvalidCouponException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExistCouponException.class)
    public ResponseEntity<ErrorResponse> handleExistCouponException(ExistCouponException ex) {
        ErrorResponse errorResponse = new ErrorResponse("ExistCouponException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CannotUseCouponException.class)
    public ResponseEntity<ErrorResponse> handleCannotUseCouponException(CannotUseCouponException ex) {
        ErrorResponse errorResponse = new ErrorResponse("CannotUseCouponException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InsufficientPointException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPointException(InsufficientPointException ex) {
        ErrorResponse errorResponse = new ErrorResponse("InsufficientPointException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(GoodsOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleGoodsOutOfStockException(GoodsOutOfStockException ex) {
        ErrorResponse errorResponse = new ErrorResponse("GoodsOutOfStockException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ExpiredCouponException.class)
    public ResponseEntity<ErrorResponse> handleExpiredCouponException(ExpiredCouponException ex) {
        ErrorResponse errorResponse = new ErrorResponse("ExpiredCouponException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CouponOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleCouponOutOfStockException(CouponOutOfStockException ex) {
        ErrorResponse errorResponse = new ErrorResponse("CouponOutOfStockException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("Exception", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}