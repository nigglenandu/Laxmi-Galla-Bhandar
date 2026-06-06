package com.laxmi.galla.core.pagination;

import com.laxmi.galla.core.dto.response.GroupedPage;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Standardized, fully immutable paginated response for all APIs.
 *
 * <h3>Supported Modes</h3>
 * <ul>
 *   <li><b>OFFSET</b>: classic page/size (currentPage, totalPages meaningful)</li>
 *   <li><b>CURSOR</b>: keyset/cursor-based (nextCursor/previousCursor meaningful)</li>
 * </ul>
 * <p>
 * Rules:
 * - Do not mix OFFSET and CURSOR in the same response
 * - In CURSOR mode, currentPage and totalPages are informational (often null)
 * - All collections are immutable (copyOf enforced at construction)
 * - links and metadata are always non-null (empty maps if not set)
 * - pageSize must be > 0
 * - In OFFSET mode, currentPage must be >= 1
 * - mode must not be null
 */
@Schema(description = "Standard paginated response – immutable & type-safe")
public record PageResponse<T>(
        @Schema(description = "Records on current page (immutable list)")
        List<T> content,

        @Schema(description = "Current page number (1-based for OFFSET mode)", example = "1", nullable = true)
        Integer currentPage,

        @Schema(description = "Page size", example = "10")
        int pageSize,

        @Schema(description = "Total records across all pages", example = "48")
        long totalElements,

        @Schema(description = "Total pages (informational in CURSOR mode)", example = "5", nullable = true)
        Integer totalPages,

        @Schema(description = "Is this the first page?", example = "true")
        boolean first,

        @Schema(description = "Is this the last page?", example = "false")
        boolean last,

        @Schema(description = "Has next page?", example = "true")
        boolean hasNext,

        @Schema(description = "Has previous page?", example = "false")
        boolean hasPrevious,

        @Schema(description = "Next cursor token (CURSOR mode only)", nullable = true)
        String nextCursor,

        @Schema(description = "Previous cursor token (CURSOR mode only)", nullable = true)
        String previousCursor,

        @Schema(description = "Optional HATEOAS-style navigation links")
        Map<String, String> links,

        @Schema(description = "Optional extra metadata (facets, aggregations, debug info)")
        Map<String, Object> metadata,

        @Schema(description = "Pagination strategy used")
        PaginationMode mode
) {

    public enum PaginationMode {
        OFFSET,
        CURSOR
    }

    // Compact constructor – enforces immutability & contract rules
    public PageResponse {
        // 0. Mode must not be null (first check to prevent NPE)
        if (mode == null) {
            throw new IllegalArgumentException("Pagination mode must not be null");
        }

        // 1. Defensive immutability (protect against direct misuse)
        content = (content != null) ? List.copyOf(content) : List.of();
        links = (links != null) ? Map.copyOf(links) : Map.of();
        metadata = (metadata != null) ? Map.copyOf(metadata) : Map.of();


        // 2. Validate pageSize > 0
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }

        if (totalPages != null && totalPages < 0) {
            throw new IllegalArgumentException("totalPages cannot be negative");
        }

        // 3. Validate currentPage >= 1 in OFFSET mode
//        if (mode == PaginationMode.OFFSET) {
//            if (currentPage == null || currentPage < 1) {
//                throw new IllegalArgumentException("currentPage must be >= 1 in OFFSET mode");
//            }
//        }
        // 3. Validate currentPage >= 1 in OFFSET mode
        if (mode == PaginationMode.OFFSET && totalPages != null && totalPages > 0 && currentPage > totalPages) {
            throw new IllegalArgumentException(
                    String.format("currentPage (%d) cannot exceed totalPages (%d)", currentPage, totalPages));
        }

        // 4. CURSOR mode consistency
        if (mode == PaginationMode.CURSOR) {
            if (currentPage != null || totalPages != null) {
                throw new IllegalArgumentException("currentPage and totalPages should be null in CURSOR mode");
            }
        }


        // Optional ultra-defensive check: currentPage ≤ totalPages in OFFSET mode
        // Uncomment if you want strict consistency (prevents illogical pages)
        /*
        if (mode == PaginationMode.OFFSET && totalPages != null && currentPage > totalPages && totalPages > 0) {
            throw new IllegalArgumentException("currentPage cannot exceed totalPages");
        }
        */
    }

    // ────────────────────────────────────────────────────────────────
    // Factories – Spring Data Page (OFFSET mode)
    // ────────────────────────────────────────────────────────────────

    //    public static <T> PageResponse<T> fromPage(Page<T> page) {
//        return new PageResponse<>(
//                page.getContent(),
//                page.getNumber() + 1,
//                page.getSize(),
//                page.getTotalElements(),
//                page.getTotalPages(),
//                page.isFirst(),
//                page.isLast(),
//                page.hasNext(),
//                page.hasPrevious(),
//                null,
//                null,
//                Map.of(),
//                Map.of(),
//                PaginationMode.OFFSET
//        );
//    }
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

    // ────────────────────────────────────────────────────────────────
    // Manual / List-based paging (OFFSET mode)
    // ────────────────────────────────────────────────────────────────

    //    public static <T> PageResponse<T> of(
//            List<T> content,
//            int currentPage,    // 1-based
//            int pageSize,
//            long totalElements) {
//
//        // Simplified totalPages (pageSize > 0 guaranteed)
//        int totalPages = totalElements == 0 ? 0 :
//                (int) Math.ceil((double) totalElements / pageSize);
//
//        return new PageResponse<>(
//                content,
//                currentPage,
//                pageSize,
//                totalElements,
//                totalPages,
//                currentPage == 1,
//                currentPage >= totalPages,
//                currentPage < totalPages,
//                currentPage > 1,
//                null,
//                null,
//                Map.of(),
//                Map.of(),
//                PaginationMode.OFFSET
//        );
//    }
    public static <T> PageResponse<T> of(
            List<T> content,
            int currentPage,
            int pageSize,
            long totalElements) {

        if (currentPage < 1) {
            throw new IllegalArgumentException("currentPage must be >= 1");
        }

        int totalPages = totalElements == 0 ? 0 :
                (int) Math.ceil((double) totalElements / pageSize);

        return createOffsetPage(content, currentPage, pageSize, totalElements, totalPages);
    }

    // ────────────────────────────────────────────────────────────────
    // Cursor-based pagination
    // ────────────────────────────────────────────────────────────────

    public static <T> PageResponse<T> fromCursor(
            List<T> content,
            String nextCursor,
            String previousCursor,
            int pageSize,
            long totalElements) {

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
                PaginationMode.CURSOR
        );
    }

    // ────────────────────────────────────────────────────────────────
    // Grouped / aggregated data (Map<K, List<V>>)
    // ────────────────────────────────────────────────────────────────
    // Note: Pages over groups (map.size()), not inner elements.
    // If you need to page over flattened inner items, flatten the map first.
//    public static <K, V> PageResponse<GroupedPage<K, V>> fromMap(
//            Map<K, List<V>> map,
//            int currentPage,
//            int pageSize) {
//
//        List<Map.Entry<K, List<V>>> entries = List.copyOf(map.entrySet());
//
//        int start = (currentPage - 1) * pageSize;
//        int end = Math.min(start + pageSize, entries.size());
//
//        List<GroupedPage<K, V>> pagedGroups = (start >= entries.size())
//                ? List.of()
//                : entries.subList(start, end).stream()
//                .map(e -> new GroupedPage<>(e.getKey(), e.getValue()))
//                .toList();
//
//        long totalElements = map.values().stream().mapToLong(List::size).sum();
//
//        // Simplified totalPages (pageSize > 0 guaranteed)
//        int totalPages = (int) Math.ceil((double) map.size() / pageSize);
//
//        return new PageResponse<>(
//                pagedGroups,
//                currentPage,
//                pageSize,
//                totalElements,
//                totalPages,
//                currentPage == 1,
//                currentPage >= totalPages,
//                currentPage < totalPages,
//                currentPage > 1,
//                null, null,
//                Map.of(),
//                Map.of(),
//                PaginationMode.OFFSET
//        );
//    }

    public static <K, V> PageResponse<GroupedPage<K, V>> fromMap(
            Map<K, List<V>> map,
            int currentPage,
            int pageSize) {

        if (map == null || map.isEmpty()) {
            return PageResponse.empty(currentPage, pageSize);
        }

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

    // ────────────────────────────────────────────────────────────────
    // Empty page
    // ────────────────────────────────────────────────────────────────

//    public static <T> PageResponse<T> empty(int currentPage, int pageSize) {
//        boolean isFirst = currentPage <= 1;
//        boolean isLast = true; // empty → always last
//
//        return new PageResponse<>(
//                List.of(),
//                currentPage,
//                pageSize,
//                0L,
//                0,
//                isFirst,
//                isLast,
//                false,
//                currentPage > 1,
//                null,
//                null,
//                Map.of(),
//                Map.of(),
//                PaginationMode.OFFSET
//        );
//    }

    // ────────────────────────────────────────────────────────────────
// Empty page
// ────────────────────────────────────────────────────────────────

    public static <T> PageResponse<T> empty(int currentPage, int pageSize) {
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
                PaginationMode.OFFSET
        );
    }

    // ────────────────────────────────────────────────────────────────
    // Optional HATEOAS links builder (call in controller)
    // ────────────────────────────────────────────────────────────────

//    public PageResponse<T> withLinks(String baseUrl) {
//        // Skip if not OFFSET mode or missing page info
//        if (mode != PaginationMode.OFFSET || currentPage == null || totalPages == null) {
//            return this;
//        }
//
//        Map<String, String> linksMap = new HashMap<>();
//        String query = "?page=%d&size=%d";
//
//        linksMap.put("self", baseUrl + query.formatted(currentPage, pageSize));
//        if (hasNext) linksMap.put("next", baseUrl + query.formatted(currentPage + 1, pageSize));
//        if (hasPrevious) linksMap.put("prev", baseUrl + query.formatted(currentPage - 1, pageSize));
//        linksMap.put("first", baseUrl + query.formatted(1, pageSize));
//        linksMap.put("last", baseUrl + query.formatted(totalPages, pageSize));
//
//        return new PageResponse<>(
//                content, currentPage, pageSize, totalElements, totalPages,
//                first, last, hasNext, hasPrevious,
//                nextCursor, previousCursor,
//                Map.copyOf(linksMap),
//                metadata,
//                mode
//        );
//    }

    public PageResponse<T> withLinks(String baseUrl) {
        // Skip if not OFFSET mode or missing page info
        if (mode != PaginationMode.OFFSET || currentPage == null || totalPages == null) {
            return this;
        }

        // Extra safety for empty results
        if (totalPages == 0) {
            return this;
        }

        Map<String, String> linksMap = new HashMap<>();
        String query = "?page=%d&size=%d";

        linksMap.put("self", baseUrl + query.formatted(currentPage, pageSize));
        if (hasNext) linksMap.put("next", baseUrl + query.formatted(currentPage + 1, pageSize));
        if (hasPrevious) linksMap.put("prev", baseUrl + query.formatted(currentPage - 1, pageSize));
        linksMap.put("first", baseUrl + query.formatted(1, pageSize));
        linksMap.put("last", baseUrl + query.formatted(totalPages, pageSize));

        return new PageResponse<>(
                content, currentPage, pageSize, totalElements, totalPages,
                first, last, hasNext, hasPrevious,
                nextCursor, previousCursor,
                Map.copyOf(linksMap),
                metadata,
                mode
        );
    }

    // Optional: add metadata
    public PageResponse<T> withMetadata(Map<String, Object> extraMetadata) {
        return new PageResponse<>(
                content, currentPage, pageSize, totalElements, totalPages,
                first, last, hasNext, hasPrevious,
                nextCursor, previousCursor, links,
                extraMetadata != null ? Map.copyOf(extraMetadata) : Map.of(),
                mode
        );
    }

    // ────────────────────────────────────────────────────────────────
// Private Helper
// ────────────────────────────────────────────────────────────────

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
                PaginationMode.OFFSET
        );
    }
}