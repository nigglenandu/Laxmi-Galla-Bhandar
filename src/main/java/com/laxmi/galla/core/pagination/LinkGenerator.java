package com.laxmi.galla.core.pagination;

import java.util.HashMap;
import java.util.Map;

/**
 * Dedicated service for generating pagination links.
 * Easy to extend/customize (different query param names, absolute URLs, etc.)
 */
public class LinkGenerator {

    private final String pageParam;
    private final String sizeParam;

    public LinkGenerator() {
        this("page", "size");
    }

    public LinkGenerator(String pageParam, String sizeParam) {
        this.pageParam = pageParam;
        this.sizeParam = sizeParam;
    }

    public Map<String, String> generateOffsetLinks(String baseUrl,
                                                   int currentPage,
                                                   int pageSize,
                                                   int totalPages,
                                                   boolean hasNext,
                                                   boolean hasPrevious) {

        Map<String, String> links = new HashMap<>();

        String queryTemplate = "?" + pageParam + "=%d&" + sizeParam + "=%d";

        links.put("self", baseUrl + queryTemplate.formatted(currentPage, pageSize));
        links.put("first", baseUrl + queryTemplate.formatted(1, pageSize));
        links.put("last", baseUrl + queryTemplate.formatted(totalPages, pageSize));

        if (hasNext) {
            links.put("next", baseUrl + queryTemplate.formatted(currentPage + 1, pageSize));
        }
        if (hasPrevious) {
            links.put("prev", baseUrl + queryTemplate.formatted(currentPage - 1, pageSize));
        }

        return Map.copyOf(links);
    }

    // Future extension point for cursor-based links
    public Map<String, String> generateCursorLinks(String baseUrl, String nextCursor, String prevCursor) {
        Map<String, String> links = new HashMap<>();
        if (nextCursor != null) {
            links.put("next", baseUrl + "?cursor=" + nextCursor);
        }
        if (prevCursor != null) {
            links.put("prev", baseUrl + "?cursor=" + prevCursor);
        }
        return Map.copyOf(links);
    }
}