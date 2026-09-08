package com.unsiiyat.backend.modules.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unsiiyat.backend.modules.author.AuthorDto;

public class CoupletSearchResultDto {

    private Long contentId;
    private String contentTitle;
    private Long genreId;
    private String genreName;
    private String genreSlug;
    private Long authorId;
    private String authorName;
    private AuthorDto author;
    private Long scriptId;
    private String scriptCode;
    private Integer coupletIndex;
    private List<String> lines = new ArrayList<>();
    private String coupletText;
    private String matchedLine;
    private Map<String, List<String>> linesByScript = new HashMap<>();
    private Map<String, String> coupletByScript = new HashMap<>();

    public CoupletSearchResultDto() {
    }

    public CoupletSearchResultDto(Long contentId, String contentTitle, Long genreId, String genreName,
            String genreSlug, Long authorId, String authorName, AuthorDto author, Long scriptId, String scriptCode,
            Integer coupletIndex, List<String> lines, String coupletText, String matchedLine) {
        this.contentId = contentId;
        this.contentTitle = contentTitle;
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreSlug = genreSlug;
        this.authorId = authorId;
        this.authorName = authorName;
        this.author = author;
        this.scriptId = scriptId;
        this.scriptCode = scriptCode;
        this.coupletIndex = coupletIndex;
        this.lines = lines != null ? lines : new ArrayList<>();
        this.coupletText = coupletText;
        this.matchedLine = matchedLine;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getGenreSlug() {
        return genreSlug;
    }

    public void setGenreSlug(String genreSlug) {
        this.genreSlug = genreSlug;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public AuthorDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDto author) {
        this.author = author;
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    public Integer getCoupletIndex() {
        return coupletIndex;
    }

    public void setCoupletIndex(Integer coupletIndex) {
        this.coupletIndex = coupletIndex;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public String getCoupletText() {
        return coupletText;
    }

    public void setCoupletText(String coupletText) {
        this.coupletText = coupletText;
    }

    public String getMatchedLine() {
        return matchedLine;
    }

    public void setMatchedLine(String matchedLine) {
        this.matchedLine = matchedLine;
    }

    public Map<String, List<String>> getLinesByScript() {
        return linesByScript;
    }

    public void setLinesByScript(Map<String, List<String>> linesByScript) {
        this.linesByScript = linesByScript;
    }

    public Map<String, String> getCoupletByScript() {
        return coupletByScript;
    }

    public void setCoupletByScript(Map<String, String> coupletByScript) {
        this.coupletByScript = coupletByScript;
    }
}
