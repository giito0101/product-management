package com.giitotech.product_management.testutil.repository;

import com.giitotech.product_management.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LogTestRepository extends JpaRepository<Log, Integer> {

    @Query("SELECT l FROM Log l ORDER BY l.timestamp DESC LIMIT 1")
    Log findLatestLog();
}
