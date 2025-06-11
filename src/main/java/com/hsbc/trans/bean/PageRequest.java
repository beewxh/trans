package com.hsbc.trans.bean;

import lombok.Data;

import java.util.List;

@Data
public class PageRequest {
    private int pageNumber = 0;
    private int pageSize = 10;

    public PageRequest() {
    }

    public PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = Math.max(0, pageNumber);
        this.pageSize = Math.max(1, Math.min(100, pageSize)); // 限制页面大小在1-100之间
    }

    public long getOffset() {
        return (long) pageNumber * pageSize;
    }
} 