package com.unsiiyat.backend.common.util;

import java.util.regex.Pattern;

public final class LanguageDetectorUtil {

    // Urdu / Arabic / Perso-Arabic unicode blocks (including Urdu specific letters like ٹ, ڈ, ڑ, ے, ں, etc.)
    private static final Pattern URDU_PATTERN = Pattern.compile(
            "[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]"
    );

    // Hindi / Devanagari unicode block (अ-ह, मात्राएँ, नुक्ता, etc.)
    private static final Pattern HINDI_PATTERN = Pattern.compile(
            "[\\u0900-\\u097F\\uA8E0-\\uA8FF]"
    );

    // English / Latin unicode blocks (including Roman Urdu diacritics like ā, ī, ū, etc.)
    private static final Pattern ENGLISH_PATTERN = Pattern.compile(
            "[a-zA-Z\\u00C0-\\u024F\\u1E00-\\u1EFF]"
    );

    public enum LanguageType {
        URDU("ur", "Urdu"),
        HINDI("hi", "Hindi"),
        ENGLISH("en", "English"),
        UNKNOWN("unknown", "Unknown");

        private final String code;
        private final String displayName;

        LanguageType(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private LanguageDetectorUtil() {
    }

    /**
     * Detects the language/script of the provided text.
     *
     * @param text Input string (e.g. poem title, author name, search query)
     * @return LanguageType (URDU, HINDI, ENGLISH, or UNKNOWN)
     */
    public static LanguageType detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return LanguageType.UNKNOWN;
        }

        String trimmed = text.trim();

        if (URDU_PATTERN.matcher(trimmed).find()) {
            return LanguageType.URDU;
        }

        if (HINDI_PATTERN.matcher(trimmed).find()) {
            return LanguageType.HINDI;
        }

        if (ENGLISH_PATTERN.matcher(trimmed).find()) {
            return LanguageType.ENGLISH;
        }

        return LanguageType.UNKNOWN;
    }

    /**
     * Returns the 2-letter language code ("ur", "hi", "en", or "unknown").
     *
     * @param text Input string
     * @return Language code string
     */
    public static String detectLanguageCode(String text) {
        return detectLanguage(text).getCode();
    }

    /**
     * Checks if text contains Urdu / Arabic script.
     */
    public static boolean isUrdu(String text) {
        return detectLanguage(text) == LanguageType.URDU;
    }

    /**
     * Checks if text contains Hindi / Devanagari script.
     */
    public static boolean isHindi(String text) {
        return detectLanguage(text) == LanguageType.HINDI;
    }

    /**
     * Checks if text contains English / Latin script.
     */
    public static boolean isEnglish(String text) {
        return detectLanguage(text) == LanguageType.ENGLISH;
    }

    /**
     * Normalizes text by removing diacritical marks / accents (e.g. jaañ / jaaṅ -> jaan,
     * farār -> farar, ga.ī -> ga.i, etc.) and converting to lowercase.
     *
     * @param text Input string
     * @return Normalized string in lowercase without diacritical accents
     */
    public static String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        // Normalize Unicode to canonical decomposed form (NFD)
        String nfd = java.text.Normalizer.normalize(trimmed, java.text.Normalizer.Form.NFD);
        // Remove all combining diacritical marks
        String stripped = nfd.replaceAll("\\p{M}", "");
        return stripped.toLowerCase();
    }
}
