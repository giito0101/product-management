package com.giitotech.product_management.dto;

import com.giitotech.product_management.validation.NotFutureDate;
import com.giitotech.product_management.validation.ValidDateRange;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ValidDateRange
public class ProductSearchForm extends SearchForm{
    //	開始日
    @NotFutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    //	終了日
    @NotFutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public ProductSearchForm(String search, String sortBy,
                             String direction, Integer page,
                             Integer size,
                             LocalDate startDate, LocalDate endDate) {
        super(search, sortBy, direction, page, size);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ProductSearchForm(String search, String sortBy,
                             String direction, Integer page,
                             Integer size) {
        super(search, sortBy, direction, page, size);
    }

    public ProductSearchForm() {
        super();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "ProductSearchForm{" +
                "endDate=" + endDate +
                ", startDate=" + startDate + '\'' +
                '}' + super.toString();
    }
}
