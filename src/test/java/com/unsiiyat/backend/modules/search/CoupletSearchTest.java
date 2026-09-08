package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.unsiiyat.backend.common.util.LanguageDetectorUtil;

public class CoupletSearchTest {

    @Test
    void testExtractCoupletsWithStanzas() {
        String body = """
                dil-e-nādāñ tujhe huā kyā hai
                āḳhir is dard kī davā kyā hai

                ham haiñ mushtāq aur vo be-zār
                yā ilāhī ye mājrā kyā hai

                ham ko un se vafā kī hai ummīd
                jo nahīñ jānte vafā kyā hai
                """;

        List<SearchService.ExtractedCouplet> couplets = SearchService.extractCouplets(body);
        assertEquals(3, couplets.size(), "Should extract exactly 3 couplets");

        assertEquals(1, couplets.get(0).getIndex());
        assertEquals("dil-e-nādāñ tujhe huā kyā hai", couplets.get(0).getLines().get(0));
        assertEquals("āḳhir is dard kī davā kyā hai", couplets.get(0).getLines().get(1));

        assertEquals(2, couplets.get(1).getIndex());
        assertEquals("ham haiñ mushtāq aur vo be-zār", couplets.get(1).getLines().get(0));
        assertEquals("yā ilāhī ye mājrā kyā hai", couplets.get(1).getLines().get(1));

        assertEquals(3, couplets.get(2).getIndex());
        assertEquals("ham ko un se vafā kī hai ummīd", couplets.get(2).getLines().get(0));
        assertEquals("jo nahīñ jānte vafā kyā hai", couplets.get(2).getLines().get(1));
    }

    @Test
    void testExtractCoupletsContinuousLines() {
        String body = """
                line 1 of couplet 1
                line 2 of couplet 1
                line 1 of couplet 2
                line 2 of couplet 2
                line 1 of couplet 3
                line 2 of couplet 3
                """;

        List<SearchService.ExtractedCouplet> couplets = SearchService.extractCouplets(body);
        assertEquals(3, couplets.size(), "Should group every 2 continuous lines into a couplet");
        assertEquals("line 1 of couplet 1", couplets.get(0).getLines().get(0));
        assertEquals("line 2 of couplet 1", couplets.get(0).getLines().get(1));
        assertEquals("line 1 of couplet 2", couplets.get(1).getLines().get(0));
    }

    @Test
    void testExtractCoupletsWithHtmlBreaks() {
        String body = "pehli line<br>doosri line<br><br>teesri line<br/>chauthi line";

        List<SearchService.ExtractedCouplet> couplets = SearchService.extractCouplets(body);
        assertEquals(2, couplets.size(), "Should handle <br> tags properly");
        assertEquals("pehli line", couplets.get(0).getLines().get(0));
        assertEquals("doosri line", couplets.get(0).getLines().get(1));
        assertEquals("teesri line", couplets.get(1).getLines().get(0));
        assertEquals("chauthi line", couplets.get(1).getLines().get(1));
    }

    @Test
    void testCoupletMatchingDiacriticsAndCase() {
        String body = """
                dil-e-nādāñ tujhe huā kyā hai
                āḳhir is dard kī davā kyā hai
                """;

        List<SearchService.ExtractedCouplet> couplets = SearchService.extractCouplets(body);
        assertEquals(1, couplets.size());

        SearchService.ExtractedCouplet couplet = couplets.get(0);

        // Search with plain english "dard"
        assertTrue(couplet.getText().toLowerCase().contains("dard"));

        // Search with diacritical "nādāñ" normalized to "nadan"
        String normLine = LanguageDetectorUtil.normalizeText(couplet.getLines().get(0));
        String normQuery = LanguageDetectorUtil.normalizeText("nadan");
        assertTrue(normLine.contains(normQuery), "Normalized search should match diacritics");
    }

    @Test
    void testOneCoupletPerGhazalAndFiveLimitLogic() {
        // Mock 10 Ghazals with their bodies
        List<String> ghazals = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ghazals.add(String.format("""
                    sher 1 line 1 of ghazal %d with ishq
                    sher 1 line 2 of ghazal %d

                    sher 2 line 1 of ghazal %d with ishq again
                    sher 2 line 2 of ghazal %d
                    """, i, i, i, i));
        }

        String searchWord = "ishq";
        int limit = 5;
        List<SearchService.ExtractedCouplet> collectedCouplets = new ArrayList<>();

        for (String ghazalBody : ghazals) {
            List<SearchService.ExtractedCouplet> couplets = SearchService.extractCouplets(ghazalBody);
            SearchService.ExtractedCouplet firstMatch = null;
            for (SearchService.ExtractedCouplet c : couplets) {
                if (c.getText().toLowerCase().contains(searchWord.toLowerCase())) {
                    firstMatch = c;
                    break; // Pick only the first match from this ghazal
                }
            }

            if (firstMatch != null) {
                collectedCouplets.add(firstMatch);
            }

            if (collectedCouplets.size() >= limit) {
                break; // Stop at 5 couplets
            }
        }

        assertEquals(5, collectedCouplets.size(), "Must collect exactly 5 couplets across ghazals");
        // Each collected couplet should be from index 1 (the first matching sher of each ghazal)
        for (SearchService.ExtractedCouplet c : collectedCouplets) {
            assertEquals(1, c.getIndex(), "Must take only 1 couplet from each ghazal");
        }
    }

    @Test
    void testCoupletsSearchResponseDtoFields() {
        CoupletsSearchResponseDto response = new CoupletsSearchResponseDto("ishq", "en", "English");
        response.setGenreId(1L);
        response.setGenreName("Ghazal");
        response.setGenreSlug("ghazal");
        response.setPage(0);
        response.setPageSize(5);
        response.setTotalCount(12);
        response.setTotalPages(3);
        response.setHasMore(true);

        assertEquals("ishq", response.getText());
        assertEquals("en", response.getDetectedScript());
        assertEquals("English", response.getDetectedLanguage());
        assertEquals(1L, response.getGenreId());
        assertEquals("Ghazal", response.getGenreName());
        assertEquals("ghazal", response.getGenreSlug());
        assertEquals(12, response.getTotalCount());
        assertEquals(3, response.getTotalPages());
        assertTrue(response.isHasMore());
    }

    public static void main(String[] args) {
        CoupletSearchTest test = new CoupletSearchTest();
        test.testExtractCoupletsWithStanzas();
        test.testExtractCoupletsContinuousLines();
        test.testExtractCoupletsWithHtmlBreaks();
        test.testCoupletMatchingDiacriticsAndCase();
        test.testOneCoupletPerGhazalAndFiveLimitLogic();
        test.testCoupletsSearchResponseDtoFields();
        System.out.println("ALL COUPLET SEARCH TESTS PASSED SUCCESSFULLY!");
    }
}
