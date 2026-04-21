package com.giitotech.product_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.giitotech.product_management.dto.UserRolesDto;
import java.util.List;

@Mapper
public interface UserMapper {

    List<UserRolesDto> findUserByKeywords(
            @Param("keywords") List<String> keywords,
            @Param("sortBy") String sortBy,
            @Param("direction") String direction,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int countUserByKeywords(@Param("keywords") List<String> keywords);
}