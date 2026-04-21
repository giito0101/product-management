package com.giitotech.product_management.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DataTimeUtil {
    
    public static Timestamp getTimestampInJapanTime() {
        // 日本標準時の現在時刻を取得
        ZonedDateTime nowJST = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        
        // LocalDateTimeを取得
        LocalDateTime localDateTime = nowJST.toLocalDateTime();
        
        // LocalDateTimeからTimestampを作成
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        
        // ナノ秒を設定
        timestamp.setNanos(localDateTime.getNano());

        return timestamp;
    }
}
