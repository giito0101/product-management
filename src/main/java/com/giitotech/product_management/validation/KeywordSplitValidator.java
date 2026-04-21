package com.giitotech.product_management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class KeywordSplitValidator implements ConstraintValidator<KeywordSplit, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
	    // Nullチェックを追加
	    if (value == null) {
	        return true; // nullは検証対象外とする（必要に応じてfalseにする）
	    }
		
		// 入力された検索ワードをチェック
		String[] keywords = value.trim()
				.replace("　", " ") // 全角スペースを半角に変換
				.split("\\s+"); // 半角スペースで分割
		
		// キーワードの数をチェック
		if (keywords.length <= 10 ) {
			return true;
		}
		return false;
	}

}
