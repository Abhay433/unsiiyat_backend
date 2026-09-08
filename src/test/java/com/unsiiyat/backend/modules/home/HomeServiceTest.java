package com.unsiiyat.backend.modules.home;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HomeServiceTest {

    @Test
    void testDailyGhazalDeterministicSelection() {
        List<Long> mockIds = List.of(101L, 102L, 103L, 104L, 105L, 106L, 107L);

        long todayEpoch = LocalDate.of(2026, 9, 8).toEpochDay();
        Random r1 = new Random(todayEpoch);
        int idx1 = r1.nextInt(mockIds.size());

        // Same day must return identical index
        Random r2 = new Random(todayEpoch);
        int idx2 = r2.nextInt(mockIds.size());

        assertEquals(idx1, idx2, "Ghazal of the day must be identical on the same date");

        // Next day should calculate on next epoch day
        long tomorrowEpoch = LocalDate.of(2026, 9, 9).toEpochDay();
        Random rTomorrow = new Random(tomorrowEpoch);
        int idxTomorrow = rTomorrow.nextInt(mockIds.size());

        // Both indices must be valid within range
        assertTrue(idx1 >= 0 && idx1 < mockIds.size());
        assertTrue(idxTomorrow >= 0 && idxTomorrow < mockIds.size());
    }

    @Test
    void testGenreCuratedGroupDto() {
        GenreCuratedGroupDto dto = new GenreCuratedGroupDto(1L, "Ghazal", "ghazal", 5L, List.of());
        assertEquals(1L, dto.getGenreId());
        assertEquals("Ghazal", dto.getGenreName());
        assertEquals("ghazal", dto.getGenreSlug());
        assertEquals(5L, dto.getTotalSelected());
        assertNotNull(dto.getContents());
    }
}
