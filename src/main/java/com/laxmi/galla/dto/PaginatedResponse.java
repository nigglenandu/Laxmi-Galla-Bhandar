package com.laxmi.galla.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

//@Data
////@AllArgsConstructor
//public class PaginatedResponse<T> {
//    private List<T> content;  //data
//    private int pageNumber;        // current page number
//    private int pageSize;          // size of page
//    private long totalElements;    // total number of records
//    private int totalPages;        // total pages
//    private boolean last;
////    success,error, default page = 1, pg size 10
//
//    public PaginatedResponse(List<T> content, boolean last, int pageNumber, int pageSize, long totalElements, int totalPages) {
//        this.content = content;
//        this.last = last;
//        this.pageNumber = pageNumber;
//        this.pageSize = pageSize;
//        this.totalElements = totalElements;
//        this.totalPages = totalPages;
//    }
//}


// 1.this record
public record PaginatedResponse<T>(
        List<T> content,
        int pageNumber,           // 0-based (Spring standard)
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious,
        int currentPage           // 1-based for frontend (optional but nice)
) {
    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious(),
                page.getNumber() + 1
        );
    }
}