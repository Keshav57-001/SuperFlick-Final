package com.superflick.shared.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Standardised paginated response wrapper.
 *
 * Wraps Spring Data's Page<T> into a predictable JSON shape so the
 * frontend doesn't have to deal with Spring's internal page structure.
 *
 * Example JSON:
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 142,
 *   "totalPages": 8,
 *   "first": true,
 *   "last": false
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    /**
     * Factory method — converts a Spring Data Page into PageResponse.
     *
     * @param page Spring Data Page object
     * @param <T>  the DTO type in the page content
     * @return wrapped PageResponse
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}