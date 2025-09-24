package shared.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        int currentPage,
        int totalPages,
        long totalItems
) {}
