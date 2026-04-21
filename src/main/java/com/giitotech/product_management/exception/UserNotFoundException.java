package com.giitotech.product_management.exception;

public class UserNotFoundException extends RuntimeException {
    // コンストラクタ
    public UserNotFoundException(String message) {
        super(message); // メッセージを親クラスに渡す
    }

    // 必要に応じて、詳細なエラーメッセージやその他の処理を追加できます
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}