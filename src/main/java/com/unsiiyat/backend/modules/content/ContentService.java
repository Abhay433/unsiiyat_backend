package com.unsiiyat.backend.modules.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.unsiiyat.backend.common.exceptions.ResourceNotFoundException;
import com.unsiiyat.backend.common.response.PagedResponse;
import com.unsiiyat.backend.modules.author.AuthorEntity;
import com.unsiiyat.backend.modules.author.AuthorRepository;
import com.unsiiyat.backend.modules.author.AuthorService;
import com.unsiiyat.backend.modules.contextText.ContentTextDto;
import com.unsiiyat.backend.modules.contextText.ContentTextEntity;
import com.unsiiyat.backend.modules.contextText.ContentTextRepository;
import com.unsiiyat.backend.modules.genre.GenreEntity;
import com.unsiiyat.backend.modules.genre.GenreRepository;
import com.unsiiyat.backend.modules.genre.GenreService;
import com.unsiiyat.backend.modules.script.ScriptEntity;
import com.unsiiyat.backend.modules.script.ScriptRepository;
import com.unsiiyat.backend.modules.theme.ThemeEntity;
import com.unsiiyat.backend.modules.theme.ThemeRepository;

import jakarta.transaction.Transactional;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private ContentTextRepository contentTextRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Transactional
    public PagedResponse<ContentDto> filterContent(ContentFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<ContentEntity> page = contentRepository
                .findAll(ContentSpecification.filter(request), pageable);

        List<ContentDto> dtoList = page.getContent().stream()
                .map(content -> this.mapToContentDto(content, request.getScriptId()))
                .collect(Collectors.toList());

        Page<ContentDto> dtoPage = new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
        return PagedResponse.fromPage(dtoPage, "Contents fetched successfully");
    }

    @Transactional
    public ContentDto addOrUpdateContent(ContentDto request) {
        ContentEntity saved;
        if (request.getId() != null) {
            saved = updateContent(request);
        } else {
            saved = createContent(request);
        }

        // Save or update all incoming content texts within the same transaction
        saveOrUpdateContentTexts(saved, request);

        return mapToContentDto(saved);
    }

    private void saveOrUpdateContentTexts(ContentEntity content, ContentDto request) {
        List<ContentTextDto> incomingTexts = new ArrayList<>();
        if (request.getContentTexts() != null && !request.getContentTexts().isEmpty()) {
            incomingTexts.addAll(request.getContentTexts());
        } else if (request.getPrimaryText() != null) {
            incomingTexts.add(request.getPrimaryText());
        }

        for (ContentTextDto textDto : incomingTexts) {
            // Strictly only save texts where actual body content is provided
            if (textDto == null || textDto.getBody() == null || textDto.getBody().trim().isEmpty()) {
                continue;
            }

            ScriptEntity script = resolveScript(textDto.getScriptId(), textDto.getTitle(), textDto.getBody());
            if (script == null) {
                continue;
            }

            String textTitle = (textDto.getTitle() != null && !textDto.getTitle().trim().isEmpty())
                    ? textDto.getTitle().trim()
                    : (request.getTitle() != null ? request.getTitle().trim() : "");

            String textBody = textDto.getBody().trim();

            // Look up existing content text for this (content_id, script_id) to avoid
            // duplicate key violations
            java.util.Optional<ContentTextEntity> existingOpt = contentTextRepository
                    .findByContentIdAndScriptId(content.getId(), script.getId());
            ContentTextEntity entity;
            if (existingOpt.isPresent()) {
                entity = existingOpt.get();
            } else if (textDto.getId() != null) {
                entity = contentTextRepository.findById(textDto.getId()).orElseGet(ContentTextEntity::new);
            } else {
                entity = new ContentTextEntity();
            }

            entity.setContent(content);
            entity.setScript(script);
            entity.setTitle(textTitle);
            entity.setBody(textBody);

            contentTextRepository.save(entity);
        }
    }

    private ScriptEntity resolveScript(Long scriptId, String title, String body) {
        if (scriptId != null) {
            return scriptRepository.findById(scriptId)
                    .orElseGet(() -> {
                        String defaultCode = scriptId == 1L ? "ur"
                                : (scriptId == 2L ? "hi" : (scriptId == 3L ? "en" : null));
                        String defaultName = scriptId == 1L ? "Urdu"
                                : (scriptId == 2L ? "Hindi" : (scriptId == 3L ? "English" : null));
                        if (defaultCode != null) {
                            return scriptRepository.findByCode(defaultCode)
                                    .orElseGet(() -> scriptRepository.save(new ScriptEntity(defaultCode, defaultName,
                                            java.time.LocalDateTime.now(), java.time.LocalDateTime.now())));
                        }
                        return null;
                    });
        }

        // Fallback: detect language from text if scriptId was not provided
        String detectedCode = com.unsiiyat.backend.common.util.LanguageDetectorUtil
                .detectLanguageCode((title != null ? title : "") + " " + (body != null ? body : ""));
        if (detectedCode != null && !"unknown".equalsIgnoreCase(detectedCode)) {
            return scriptRepository.findByCode(detectedCode).orElse(null);
        }
        return null;
    }

    @Transactional
    public ContentEntity createContent(ContentDto request) {
        ContentEntity entity = new ContentEntity();
        if (request.getGenreId() != null) {
            GenreEntity genre = genreRepository.findById(request.getGenreId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Genre not found with id: " + request.getGenreId()));
            entity.setGenre(genre);
        }
        if (request.getAuthorId() != null) {
            AuthorEntity author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));
            entity.setAuthor(author);
        }

        if (request.getThemeIds() != null && !request.getThemeIds().isEmpty()) {
            Set<ThemeEntity> themes = request.getThemeIds().stream()
                    .map(id -> themeRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + id)))
                    .collect(Collectors.toSet());
            entity.setThemes(themes);
        }

        if (request.getIsSelected() != null && Boolean.TRUE.equals(request.getIsSelected())) {
            validateSelectedLimit(request.getGenreId(), null);
            entity.setIsSelected(true);
        } else {
            entity.setIsSelected(false);
        }

        return contentRepository.save(entity);
    }

    @Transactional
    public ContentEntity updateContent(ContentDto request) {
        ContentEntity entity = contentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + request.getId()));

        if (request.getGenreId() != null) {
            GenreEntity genre = genreRepository.findById(request.getGenreId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Genre not found with id: " + request.getGenreId()));
            entity.setGenre(genre);
        }
        if (request.getAuthorId() != null) {
            AuthorEntity author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));
            entity.setAuthor(author);
        }

        if (request.getThemeIds() != null) {
            Set<ThemeEntity> themes = request.getThemeIds().stream()
                    .map(id -> themeRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + id)))
                    .collect(Collectors.toSet());
            entity.setThemes(themes);
        }

        if (request.getIsSelected() != null) {
            if (Boolean.TRUE.equals(request.getIsSelected())) {
                Long targetGenreId = (request.getGenreId() != null) ? request.getGenreId()
                        : (entity.getGenre() != null ? entity.getGenre().getId() : null);
                validateSelectedLimit(targetGenreId, entity.getId());
            }
            entity.setIsSelected(request.getIsSelected());
        }

        return contentRepository.save(entity);
    }

    public long countSelectedByGenre(Long genreId) {
        if (genreId == null) {
            return 0L;
        }
        return contentRepository.countByGenreIdAndIsSelectedTrue(genreId);
    }

    private void validateSelectedLimit(Long genreId, Long currentContentId) {
        if (genreId == null) {
            return;
        }
        long count = contentRepository.countByGenreIdAndIsSelectedTrue(genreId);
        if (currentContentId != null) {
            Optional<ContentEntity> existingOpt = contentRepository.findById(currentContentId);
            if (existingOpt.isPresent() && Boolean.TRUE.equals(existingOpt.get().getIsSelected())
                    && existingOpt.get().getGenre() != null
                    && genreId.equals(existingOpt.get().getGenre().getId())) {
                return; // Already selected in this genre, count doesn't increase
            }
        }
        if (count >= 8) {
            throw new ValidationException("Cannot select more than 8 contents for this genre.");
        }
    }

    @Transactional
    public void deleteContent(ContentDto request) {
        ContentEntity entity = contentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + request.getId()));
        contentRepository.delete(entity);
    }

    public ContentDto mapToContentDto(ContentEntity entity) {
        return mapToContentDto(entity, null);
    }

    public ContentDto mapToContentDto(ContentEntity entity, Long preferredScriptId) {
        if (entity == null) {
            return null;
        }
        ContentDto dto = new ContentDto();
        dto.setId(entity.getId());
        if (entity.getGenre() != null) {
            dto.setGenreId(entity.getGenre().getId());
            dto.setGenre(genreService.mapToGenreDto(entity.getGenre()));
        }
        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
            dto.setAuthor(authorService.mapToAuthorDto(entity.getAuthor(), preferredScriptId));
        }
        if (entity.getThemes() != null) {
            dto.setThemeIds(entity.getThemes().stream().map(ThemeEntity::getId).collect(Collectors.toSet()));
        }
        dto.setIsSelected(entity.getIsSelected() != null ? entity.getIsSelected() : false);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<ContentTextEntity> textEntities = contentTextRepository.findByContentId(entity.getId());
        if (textEntities != null && !textEntities.isEmpty()) {
            List<ContentTextDto> textDtos = new ArrayList<>();
            ContentTextDto preferredText = null;
            for (ContentTextEntity t : textEntities) {
                ContentTextDto tDto = new ContentTextDto();
                tDto.setId(t.getId());
                tDto.setContentId(entity.getId());
                if (t.getScript() != null) {
                    tDto.setScriptId(t.getScript().getId());
                    if (preferredScriptId != null && t.getScript().getId().equals(preferredScriptId)) {
                        preferredText = tDto;
                    }
                }
                tDto.setTitle(t.getTitle());
                tDto.setBody(t.getBody());
                textDtos.add(tDto);
            }
            dto.setContentTexts(textDtos);
            ContentTextDto primary = preferredText != null ? preferredText : textDtos.get(0);
            dto.setPrimaryText(primary);
            if (primary != null) {
                dto.setTitle(primary.getTitle());
            }
        }

        return dto;
    }

}
