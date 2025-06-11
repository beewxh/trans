package com.hsbc.trans.vo;

import lombok.Data;
import java.util.Collections;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> content;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private boolean first;
    private boolean last;
    
    public PageResult() {
        this.content = Collections.emptyList();
    }
    
    public PageResult(List<T> content, long totalElements, PageRequest pageRequest) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageRequest.getPageNumber();
        this.pageSize = pageRequest.getPageSize();
        this.totalPages = pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
        this.first = pageNumber == 1;
        this.last = pageNumber >= totalPages - 1;
    }
    
    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }
} 