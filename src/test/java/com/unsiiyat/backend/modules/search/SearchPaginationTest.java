package com.unsiiyat.backend.modules.search;

import org.junit.jupiter.api.Test;
import com.unsiiyat.backend.common.filters.OffsetLimitPageRequest;

import static org.junit.jupiter.api.Assertions.*;

public class SearchPaginationTest {

    @Test
    void testInitialPageOffsetAndLimit() {
        int page = 0;
        Integer size = null;

        long offset;
        int limit;
        if (page == 0) {
            limit = (size != null && size > 0) ? size : 5;
            offset = 0;
        } else {
            limit = (size != null && size > 0) ? size : 10;
            offset = 5L + (long) (page - 1) * limit;
        }

        assertEquals(0, offset, "First time offset must be 0");
        assertEquals(5, limit, "First time limit must be 5");
    }

    @Test
    void testAuthorInitialPageOffsetAndLimit() {
        int page = 0;
        Integer size = null;

        long authorOffset;
        int authorLimit;
        if (page == 0) {
            authorLimit = (size != null && size > 0) ? size : 3;
            authorOffset = 0;
        } else {
            authorLimit = (size != null && size > 0) ? size : 10;
            authorOffset = 3L + (long) (page - 1) * authorLimit;
        }

        assertEquals(0, authorOffset, "Author first time offset must be 0");
        assertEquals(3, authorLimit, "Author first time limit must be 3");
    }

    @Test
    void testAuthorTotalPagesCalculation() {
        assertEquals(0, SearchService.calculateAuthorTotalPages(0));
        assertEquals(1, SearchService.calculateAuthorTotalPages(2));
        assertEquals(1, SearchService.calculateAuthorTotalPages(3));
        assertEquals(2, SearchService.calculateAuthorTotalPages(4));   // 3 on page 0, 1 on page 1
        assertEquals(2, SearchService.calculateAuthorTotalPages(13));  // 3 on page 0, 10 on page 1
        assertEquals(3, SearchService.calculateAuthorTotalPages(14));  // 3 on p0, 10 on p1, 1 on p2
    }

    @Test
    void testNextPagesOffsetAndLimit() {
        // Page 1: 10 items starting right after the initial 5 (offset = 5, limit = 10)
        int page1 = 1;
        long offset1 = 5L + (long) (page1 - 1) * 10;
        assertEquals(5, offset1);

        // Page 2: 10 items starting at 15 (offset = 15, limit = 10)
        int page2 = 2;
        long offset2 = 5L + (long) (page2 - 1) * 10;
        assertEquals(15, offset2);

        // Page 3: 10 items starting at 25 (offset = 25, limit = 10)
        int page3 = 3;
        long offset3 = 5L + (long) (page3 - 1) * 10;
        assertEquals(25, offset3);
    }

    @Test
    void testTotalPagesCalculation() {
        assertEquals(0, SearchService.calculateTotalPages(0));
        assertEquals(1, SearchService.calculateTotalPages(3));
        assertEquals(1, SearchService.calculateTotalPages(5));
        assertEquals(2, SearchService.calculateTotalPages(6));   // 5 on page 0, 1 on page 1
        assertEquals(2, SearchService.calculateTotalPages(15));  // 5 on page 0, 10 on page 1
        assertEquals(3, SearchService.calculateTotalPages(16));  // 5 on p0, 10 on p1, 1 on p2
        assertEquals(3, SearchService.calculateTotalPages(25));  // 5 on p0, 10 on p1, 10 on p2
        assertEquals(4, SearchService.calculateTotalPages(26));  // 5 on p0, 10 on p1, 10 on p2, 1 on p3
    }

    @Test
    void testOffsetLimitPageRequest() {
        OffsetLimitPageRequest request = new OffsetLimitPageRequest(5, 10);
        assertEquals(5, request.getOffset());
        assertEquals(10, request.getPageSize());
        assertTrue(request.hasPrevious());

        OffsetLimitPageRequest initial = new OffsetLimitPageRequest(0, 5);
        assertEquals(0, initial.getOffset());
        assertEquals(5, initial.getPageSize());
        assertFalse(initial.hasPrevious());
    }

    @Test
    void testSearchRequestDtoDefaults() {
        SearchRequestDto dto = new SearchRequestDto("ghalib");
        assertEquals("ghalib", dto.getText());
        assertEquals(0, dto.getPage());
        assertNull(dto.getSize());

        dto.setPage(1);
        assertEquals(1, dto.getPage());
        dto.setSize(10);
        assertEquals(10, dto.getSize());
    }

    @Test
    void testGenreSearchResultDtoPaginationFields() {
        GenreSearchResultDto dto = new GenreSearchResultDto();
        dto.setTotalCount(25);
        dto.setPage(0);
        dto.setPageSize(5);
        dto.setTotalPages(SearchService.calculateTotalPages(25));
        dto.setHasMore(true);

        assertEquals(0, dto.getPage());
        assertEquals(5, dto.getPageSize());
        assertEquals(3, dto.getTotalPages());
        assertTrue(dto.isHasMore());
    }
}
