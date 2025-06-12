package com.hsbc.trans.vo;

import lombok.Data;

/**
 * Represents a request for a paginated result, containing pagination parameters.
 * Used to specify the desired page number and page size for paginated queries.
 */
@Data
public class PageRequest {
    /**
     * The page number to retrieve (0-based)
     * Defaults to 0 (first page)
     */
    private int pageNumber = 0;

    /**
     * The number of items per page
     * Defaults to 10 items per page
     */
    private int pageSize = 10;

    /**
     * Constructs a page request with default values
     * (page number = 0, page size = 10)
     */
    public PageRequest() {
    }

    /**
     * Constructs a page request with the specified page number and size
     *
     * @param pageNumber The page number to retrieve (0-based, will be adjusted to minimum 0)
     * @param pageSize The number of items per page (will be constrained between 1 and 100)
     */
    public PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = Math.max(0, pageNumber);
        this.pageSize = Math.max(1, Math.min(100, pageSize)); // Limit page size between 1-100
    }

    /**
     * Calculates the offset for database queries based on page number and size
     *
     * @return The offset value for database queries
     */
    public long getOffset() {
        return (long) pageNumber * pageSize;
    }
} 