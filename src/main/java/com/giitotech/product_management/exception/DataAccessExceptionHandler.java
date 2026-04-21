package com.giitotech.product_management.exception;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class DataAccessExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DataAccessExceptionHandler.class);

    // データベース接続に関する例外（JDBC接続エラーなど）
    @ExceptionHandler(JDBCConnectionException.class)
    public ResponseEntity<String> handleDatabaseConnectionException(JDBCConnectionException exc) {
        String errorMessage = "Unable to connect to the database. Please try again later.";
        logger.error(errorMessage, exc);
        return new ResponseEntity<>(errorMessage, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // JPAの一般的なエラー
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<String> handlePersistenceException(PersistenceException exc) {
        String errorMessage = "An error occurred while accessing the database. Please try again later.";
        logger.error(errorMessage, exc);
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // クエリタイムアウト
    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<String> handleQueryTimeoutException(QueryTimeoutException exc) {
        String errorMessage = "The query has timed out. Please try again later.";
        logger.error(errorMessage, exc);
        return new ResponseEntity<>(errorMessage, HttpStatus.REQUEST_TIMEOUT);
    }

    // データアクセスエラー（Spring Data JPA で発生する一般的なエラー）
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException exc) {
        String errorMessage = "A database error occurred. Please try again later.";
        logger.error(errorMessage, exc);
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 制約違反エラー（例: UNIQUE制約、NOT NULL制約など）
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException exc) {
        String errorMessage = "A database constraint was violated. Please check the data and try again.";
        logger.error(errorMessage, exc);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // その他のJPAシステムエラー（JpaSystemExceptionなど）
    @ExceptionHandler(org.springframework.orm.jpa.JpaSystemException.class)
    public ResponseEntity<String> handleJpaSystemException(org.springframework.orm.jpa.JpaSystemException exc) {
        String errorMessage = "A system error occurred while accessing the database.";
        logger.error(errorMessage, exc);
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}