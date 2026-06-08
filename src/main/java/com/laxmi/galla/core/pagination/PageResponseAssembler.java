package com.laxmi.galla.core.pagination;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for enriching PageResponse with links and metadata.
 * Keeps the DTO (record) clean and immutable.
 */
public final class PageResponseAssembler<T> {

    private final PageResponse<T> source;
    private Map<String, String> links = Map.of();
    private Map<String, Object> metadata = Map.of();

    private PageResponseAssembler(PageResponse<T> source) {
        this.source = source;
    }

    public static <T> PageResponseAssembler<T> of(PageResponse<T> page) {
        if (page == null) throw new IllegalArgumentException("page cannot be null");
        return new PageResponseAssembler<>(page);
    }

    /**
     * Adds HATEOAS-style navigation links for OFFSET pagination.
     */
    public PageResponseAssembler<T> withLinks(String baseUrl) {
        return withLinks(baseUrl, new LinkGenerator());
    }

    /**
     * Allows custom LinkGenerator (e.g. for different URL strategies, query param names, etc.)
     */
    public PageResponseAssembler<T> withLinks(String baseUrl, LinkGenerator linkGenerator) {
        if (source.mode() != PageResponse.PaginationMode.OFFSET ||
                source.currentPage() == null ||
                source.totalPages() == null ||
                source.totalPages() == 0) {
            return this;
        }

        this.links = linkGenerator.generateOffsetLinks(
                baseUrl,
                source.currentPage(),
                source.pageSize(),
                source.totalPages(),
                source.hasNext(),
                source.hasPrevious()
        );
        return this;
    }

    public PageResponseAssembler<T> withMetadata(Map<String, Object> extraMetadata) {
        this.metadata = (extraMetadata != null) ? Map.copyOf(extraMetadata) : Map.of();
        return this;
    }

    public PageResponseAssembler<T> withMetadata(String key, Object value) {
        Map<String, Object> copy = new HashMap<>(this.metadata);
        copy.put(key, value);
        this.metadata = Map.copyOf(copy);
        return this;
    }

    public PageResponse<T> assemble() {
        return new PageResponse<>(
                source.content(),
                source.currentPage(),
                source.pageSize(),
                source.totalElements(),
                source.totalPages(),
                source.first(),
                source.last(),
                source.hasNext(),
                source.hasPrevious(),
                source.nextCursor(),
                source.previousCursor(),
                links,
                metadata,
                source.mode()
        );
    }
}