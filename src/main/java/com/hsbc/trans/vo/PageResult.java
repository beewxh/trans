package com.hsbc.trans.vo;

import lombok.Data;
import java.util.Collections;
import java.util.List;

/**
 * Represents a paginated result containing a list of items and pagination metadata.
 *
 * @param <T> The type of elements in the page
 */
@Data
public class PageResult<T> {
    /**
     * The list of items in the current page
     */
    private List<T> content;

    /**
     * The total number of elements across all pages
     */
    private long totalElements;

    /**
     * The current page number (0-based)
     */
    private int pageNumber;

    /**
     * The size of each page
     */
    private int pageSize;

    /**
     * The total number of pages
     */
    private int totalPages;

    /**
     * Whether this is the first page
     */
    private boolean first;

    /**
     * Whether this is the last page
     */
    private boolean last;
    
    /**
     * Constructs an empty page result
     */
    public PageResult() {
        this.content = Collections.emptyList();
    }
    
    /**
     * Constructs a page result with the given content and pagination information
     *
     * @param content The list of items in the current page
     * @param totalElements The total number of elements across all pages
     * @param pageRequest The page request containing pagination parameters
     */
    public PageResult(List<T> content, long totalElements, PageRequest pageRequest) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageRequest.getPageNumber();
        this.pageSize = pageRequest.getPageSize();
        this.totalPages = pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
        this.first = pageNumber == 1;
        this.last = pageNumber >= totalPages - 1;
    }
    
    /**
     * Creates an empty page result
     *
     * @param <T> The type of elements in the page
     * @return An empty page result
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }
} 