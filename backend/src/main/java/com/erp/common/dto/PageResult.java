package com.erp.common.dto;

import java.util.List;

public class PageResult<T> {
    private Long total;
    private Integer pageNo;
    private Integer pageSize;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, Integer pageNo, Integer pageSize, List<T> records) {
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.records = records;
    }

    public static <T> PageResult<T> of(long total, int pageNo, int pageSize, List<T> records) {
        return new PageResult<>(total, pageNo, pageSize, records);
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
