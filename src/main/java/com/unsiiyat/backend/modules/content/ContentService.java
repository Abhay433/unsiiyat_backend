package com.unsiiyat.backend.modules.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Transactional
    public PagedResponse<ContentDto> filterContent(ContentFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<ContentEntity> page = contentRepository
                .findAll(ContentSpecification.filter(request), pageable);

        List<ContentDto> dtoList = page.getContent().stream().map(this::mapToContentDto)
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
        return mapToContentDto(saved);
    }

    @Transactional
    public ContentEntity createContent(ContentDto request) {
        ContentEntity entity = new ContentEntity();
        if (request.getGenreId() != null) {
            GenreEntity genre = genreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + request.getGenreId()));
            entity.setGenre(genre);
        }
        if (request.getAuthorId() != null) {
            AuthorEntity author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));
            entity.setAuthor(author);
        }
        entity.setTitle(request.getTitle());

        if (request.getThemeIds() != null && !request.getThemeIds().isEmpty()) {
            Set<ThemeEntity> themes = request.getThemeIds().stream()
                    .map(id -> themeRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + id)))
                    .collect(Collectors.toSet());
            entity.setThemes(themes);
        }

        return contentRepository.save(entity);
    }

    @Transactional
    public ContentEntity updateContent(ContentDto request) {
        ContentEntity entity = contentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + request.getId()));

        if (request.getGenreId() != null) {
            GenreEntity genre = genreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + request.getGenreId()));
            entity.setGenre(genre);
        }
        if (request.getAuthorId() != null) {
            AuthorEntity author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));
            entity.setAuthor(author);
        }
        entity.setTitle(request.getTitle());

        if (request.getThemeIds() != null) {
            Set<ThemeEntity> themes = request.getThemeIds().stream()
                    .map(id -> themeRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + id)))
                    .collect(Collectors.toSet());
            entity.setThemes(themes);
        }

        return contentRepository.save(entity);
    }

    @Transactional
    public void deleteContent(ContentDto request) {
        ContentEntity entity = contentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + request.getId()));
        contentRepository.delete(entity);
    }

    public ContentDto mapToContentDto(ContentEntity entity) {
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
            dto.setAuthor(authorService.mapToAuthorDto(entity.getAuthor()));
        }
        dto.setTitle(entity.getTitle());
        if (entity.getThemes() != null) {
            dto.setThemeIds(entity.getThemes().stream().map(ThemeEntity::getId).collect(Collectors.toSet()));
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<ContentTextEntity> textEntities = entity.getContentTexts();
        if (textEntities == null || textEntities.isEmpty()) {
            textEntities = contentTextRepository.findByContentId(entity.getId());
        }

        if (textEntities != null && !textEntities.isEmpty()) {
            List<ContentTextDto> textDtos = new ArrayList<>();
            for (ContentTextEntity t : textEntities) {
                ContentTextDto tDto = new ContentTextDto();
                tDto.setId(t.getId());
                tDto.setContentId(entity.getId());
                if (t.getScript() != null) {
                    tDto.setScriptId(t.getScript().getId());
                }
                tDto.setTitle(t.getTitle());
                tDto.setBody(t.getBody());
                textDtos.add(tDto);
            }
            dto.setContentTexts(textDtos);
            dto.setPrimaryText(textDtos.get(0));
        }

        return dto;
    }

}
