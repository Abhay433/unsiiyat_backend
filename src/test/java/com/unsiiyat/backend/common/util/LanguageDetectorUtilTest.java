package com.unsiiyat.backend.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LanguageDetectorUtilTest {

    @Test
    void testUrduDetection() {
        assertEquals(LanguageDetectorUtil.LanguageType.URDU, LanguageDetectorUtil.detectLanguage("جان ہو چکی فرار"));
        assertEquals(LanguageDetectorUtil.LanguageType.URDU, LanguageDetectorUtil.detectLanguage("محبت"));
        assertEquals("ur", LanguageDetectorUtil.detectLanguageCode("غالب"));
        assertTrue(LanguageDetectorUtil.isUrdu("میر تقی میر"));
    }

    @Test
    void testHindiDetection() {
        assertEquals(LanguageDetectorUtil.LanguageType.HINDI, LanguageDetectorUtil.detectLanguage("जान हो चुकी फरार"));
        assertEquals(LanguageDetectorUtil.LanguageType.HINDI, LanguageDetectorUtil.detectLanguage("मोहब्बत"));
        assertEquals("hi", LanguageDetectorUtil.detectLanguageCode("ग़ालिब"));
        assertTrue(LanguageDetectorUtil.isHindi("कविता"));
    }

    @Test
    void testEnglishDetection() {
        assertEquals(LanguageDetectorUtil.LanguageType.ENGLISH, LanguageDetectorUtil.detectLanguage("jaan ho chuki farar"));
        assertEquals(LanguageDetectorUtil.LanguageType.ENGLISH, LanguageDetectorUtil.detectLanguage("jaaṅ ho chukī"));
        assertEquals("en", LanguageDetectorUtil.detectLanguageCode("Ghazal"));
        assertTrue(LanguageDetectorUtil.isEnglish("Poem Title"));
    }

    @Test
    void testEnglishNormalization() {
        assertEquals("jaan", LanguageDetectorUtil.normalizeText("jaañ"));
        assertEquals("jaan", LanguageDetectorUtil.normalizeText("jaaṅ"));
        assertEquals("farar", LanguageDetectorUtil.normalizeText("farār"));
        assertEquals("ga.i", LanguageDetectorUtil.normalizeText("ga.ī"));
        assertEquals("thehar", LanguageDetectorUtil.normalizeText("ṭhehar"));
        assertEquals("dar", LanguageDetectorUtil.normalizeText("ḍar"));
        assertEquals("sabr", LanguageDetectorUtil.normalizeText("ṣabr"));
        assertEquals("zulm", LanguageDetectorUtil.normalizeText("ẓulm"));
        assertEquals("jaan ho chuki farar", LanguageDetectorUtil.normalizeText("jaañ ho chukī farār"));
    }
}
