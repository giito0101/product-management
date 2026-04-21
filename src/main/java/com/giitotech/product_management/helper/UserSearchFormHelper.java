package com.giitotech.product_management.helper;

import com.giitotech.product_management.dto.UserSearchForm;
import org.springframework.util.StringUtils;

public class UserSearchFormHelper {

    public static void fillDefaults(UserSearchForm form, String defaultSortBy,
                                    String defaultDirection, int defaultPage, int defaultSize) {
        if (!StringUtils.hasText(form.getSortBy())) {
            form.setSortBy(defaultSortBy);
        }
        if (!StringUtils.hasText(form.getDirection())) {
            form.setDirection(defaultDirection);
        }
        if (form.getPage() == null) {
            form.setPage(defaultPage);
        }
        if (form.getSize() == null) {
            form.setSize(defaultSize);
        }
    }
}