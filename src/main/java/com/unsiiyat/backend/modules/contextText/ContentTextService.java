package com.unsiiyat.backend.modules.contextText;

import java.util.List;
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
import com.unsiiyat.backend.modules.content.ContentEntity;
import com.unsiiyat.backend.modules.script.ScriptEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ContentTextService {

    @Autowired
    private ContentTextRepository contentTextRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public PagedResponse<ContentTextDto> filterContentText(ContentTextFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<ContentTextEntity> page = contentTextRepository
                .findAll(ContentTextSpecification.filter(request), pageable);

        List<ContentTextDto> dtoList = page.getContent().stream().map(this::mapToContentTextDto)
                .collect(Collectors.toList());

        Page<ContentTextDto> dtoPage = new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
        return PagedResponse.fromPage(dtoPage, "Content texts fetched successfully");
    }

    @Transactional
    public void addOrUpdateContentText(ContentTextDto request) {

        if (request.getId() != null) {
            updateContentText(request);
        } else {
            createContentText(request);
        }

    }

    @Transactional
    public void createContentText(ContentTextDto request) {
        ContentTextEntity entity = new ContentTextEntity();
        if (request.getContentId() != null) {
            entity.setContent(entityManager.getReference(ContentEntity.class, request.getContentId()));
        }
        if (request.getScriptId() != null) {
            entity.setScript(entityManager.getReference(ScriptEntity.class, request.getScriptId()));
        }
        entity.setTitle(request.getTitle());
        entity.setBody(request.getBody());
        contentTextRepository.save(entity);
    }

    @Transactional
    public void updateContentText(ContentTextDto request) {
        ContentTextEntity entity = contentTextRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ContentText not found with id: " + request.getId()));
        if (request.getContentId() != null) {
            entity.setContent(entityManager.getReference(ContentEntity.class, request.getContentId()));
        }
        if (request.getScriptId() != null) {
            entity.setScript(entityManager.getReference(ScriptEntity.class, request.getScriptId()));
        }
        entity.setTitle(request.getTitle());
        entity.setBody(request.getBody());
        contentTextRepository.save(entity);
    }

    public ContentTextDto mapToContentTextDto(ContentTextEntity entity) {
        ContentTextDto dto = new ContentTextDto();
        dto.setId(entity.getId());
        if (entity.getContent() != null) {
            dto.setContentId(entity.getContent().getId());
        }
        if (entity.getScript() != null) {
            dto.setScriptId(entity.getScript().getId());
        }
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());
        return dto;
    }

}
