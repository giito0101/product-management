package com.giitotech.product_management.exception;

public class ProductNotFoundException extends RuntimeException {
    // コンストラクタ
    public ProductNotFoundException(String message) {
        super(message); // メッセージを親クラスに渡す
    }

    // 必要に応じて、詳細なエラーメッセージやその他の処理を追加できます
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}