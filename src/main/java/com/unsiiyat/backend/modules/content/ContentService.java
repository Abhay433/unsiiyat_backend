package com.unsiiyat.backend.modules.content;

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
import com.unsiiyat.backend.modules.genre.GenreEntity;
import com.unsiiyat.backend.modules.theme.ThemeEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
    public void addOrUpdateContent(ContentDto request) {

        if (request.getId() != null) {
            updateContent(request);
        } else {
            createContent(request);
        }

    }

    @Transactional
    public void createContent(ContentDto request) {
        ContentEntity entity = new ContentEntity();
        if (request.getGenreId() != null) {
            entity.setGenre(entityManager.getReference(GenreEntity.class, request.getGenreId()));
        }
        if (request.getAuthorId() != null) {
            entity.setAuthor(entityManager.getReference(AuthorEntity.class, request.getAuthorId()));
        }
        entity.setTitle(request.getTitle());

        if (request.getThemeIds() != null && !request.getThemeIds().isEmpty()) {
            Set<ThemeEntity> themes = request.getThemeIds().stream()
                    .map(id -> entityManager.getReference(ThemeEntity.class, id))
                    .collect(Collectors.toSet());
            entity.setThemes(themes);
        }

        contentRepository.save(entity);
    }

    @Transactional
    public void updateContent(ContentDto request) {
        ContentEntity entity = contentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + request.getId()));

        if (request.getGenreId() != null) {
            entity.setGenre(entityManager.getReference(GenreEntity.class, request.getGenreId()));
        }
        if (request.getAuthorId() != null) {
            entity.setAuthor(entityManager.getReference(AuthorEntity.class, request.getAuthorId()));
        }
        entity.setTitle(request.getTitle());

        if (request.getThemeIds() != null) {
            Set<ThemeEntity> themes = request.getThemeIds().stream()
                    .map(id -> entityManager.getReference(ThemeEntity.class, id))
                    .collect(Collectors.toSet());
            entity.setThemes(themes);
        }

        contentRepository.save(entity);
    }

    public ContentDto mapToContentDto(ContentEntity entity) {
        ContentDto dto = new ContentDto();
        dto.setId(entity.getId());
        if (entity.getGenre() != null) {
            dto.setGenreId(entity.getGenre().getId());
        }
        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
        }
        dto.setTitle(entity.getTitle());
        if (entity.getThemes() != null) {
            dto.setThemeIds(entity.getThemes().stream().map(ThemeEntity::getId).collect(Collectors.toSet()));
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

}
