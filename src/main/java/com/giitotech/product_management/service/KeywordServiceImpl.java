package com.giitotech.product_management.service;

import org.springframework.stereotype.Service;

@Service
public class KeywordServiceImpl implements KeywordService{
	
    public String[] processKeywords(String keyword) {

        String[] keywords = null;

        // 空でないかチェック
        if (keyword != null && !keyword.trim().isEmpty()) {
            keywords = keyword.trim()
                .replace("　", " ") // 全角スペースを半角に変換
                .split("\\s+"); // 半角スペースで分割

            // キーワード数が10を超えた場合は無効
            if (keywords.length > 10) {
                keywords = null;
            }
        }

        return keywords;
    }
}
