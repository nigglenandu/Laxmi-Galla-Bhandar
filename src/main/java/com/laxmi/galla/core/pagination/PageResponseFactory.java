package com.laxmi.galla.core.pagination;

import com.laxmi.galla.core.dto.response.GroupedPage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PageResponseFactory {

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return createOffsetPage(
                page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public static <T, R> PageResponse<R> fromPage(Page<T> page, Function<T, R> mapper) {
        return fromPage(page.map(mapper));
    }

    public static <T> PageResponse<T> of(
            List<T> content,
            int currentPage,
            int pageSize,
            long totalElements) {

        PaginationValidator.validateOffset(currentPage, pageSize, null);

        int totalPages = totalElements == 0 ? 0 :
                (int) Math.ceil((double) totalElements / pageSize);

        return createOffsetPage(content, currentPage, pageSize, totalElements, totalPages);
    }

    public static <T> PageResponse<T> fromCursor(
            List<T> content,
            String nextCursor,
            String previousCursor,
            int pageSize,
            long totalElements) {
        PaginationValidator.validateCursor(pageSize, null, null);

        return new PageResponse<>(
                content,
                null,
                pageSize,
                totalElements,
                null,
                previousCursor == null,
                nextCursor == null,
                nextCursor != null,
                previousCursor != null,
                nextCursor,
                previousCursor,
                Map.of(),
                Map.of(),
                PageResponse.PaginationMode.CURSOR
        );
    }

    public static <K, V> PageResponse<GroupedPage<K, V>> fromMap(
            Map<K, List<V>> map,
            int currentPage,
            int pageSize) {

        if (map == null || map.isEmpty()) {
            return empty(currentPage, pageSize);
        }

        PaginationValidator.validateOffset(currentPage, pageSize, null);

        List<Map.Entry<K, List<V>>> entries = List.copyOf(map.entrySet());

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, entries.size());

        List<GroupedPage<K, V>> pagedGroups = start >= entries.size()
                ? List.of()
                : entries.subList(start, end).stream()
                .map(e -> new GroupedPage<>(e.getKey(), e.getValue()))
                .toList();

        long totalElements = map.values().stream().mapToLong(List::size).sum();
        int totalGroups = map.size();
        int totalPages = totalGroups == 0 ? 0 :
                (int) Math.ceil((double) totalGroups / pageSize);

        return createOffsetPage(pagedGroups, currentPage, pageSize, totalElements, totalPages);
    }

    public static <T> PageResponse<T> empty(int currentPage, int pageSize) {
        PaginationValidator.validateOffset(currentPage, pageSize, 0);
        // Normalize page number for empty results
        int normalizedPage = Math.max(1, currentPage);

        return new PageResponse<>(
                List.of(),
                normalizedPage,
                pageSize,
                0L,                    // totalElements
                0,                     // totalPages = 0
                true,                  // first
                true,                  // last
                false,                 // hasNext
                false,                 // hasPrevious
                null,
                null,
                Map.of(),
                Map.of(),
                PageResponse.PaginationMode.OFFSET
        );
    }

    static <T> PageResponse<T> createOffsetPage(
            List<T> content,
            int currentPage,
            int pageSize,
            long totalElements,
            int totalPages) {

        boolean isFirst = currentPage == 1;
        boolean isLast = totalPages == 0 || currentPage == totalPages;
        boolean hasNext = currentPage < totalPages;
        boolean hasPrevious = currentPage > 1;

        return new PageResponse<>(
                content,
                currentPage,
                pageSize,
                totalElements,
                totalPages,
                isFirst,
                isLast,
                hasNext,
                hasPrevious,
                null,
                null,
                Map.of(),
                Map.of(),
                PageResponse.PaginationMode.OFFSET
        );
    }
}
