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
import com.unsiiyat.backend.modules.content.ContentRepository;
import com.unsiiyat.backend.modules.script.ScriptEntity;
import com.unsiiyat.backend.modules.script.ScriptRepository;

import jakarta.transaction.Transactional;

@Service
public class ContentTextService {

    @Autowired
    private ContentTextRepository contentTextRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ScriptRepository scriptRepository;

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
    public ContentTextDto addOrUpdateContentText(ContentTextDto request) {
        // 1. Resolve Content if provided
        ContentEntity content = null;
        if (request.getContentId() != null) {
            content = contentRepository.findById(request.getContentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: "
                            + request.getContentId() + ". Please create the content record first."));
        }

        // 2. Resolve Script if provided (supporting standard fallback by code)
        ScriptEntity script = null;
        if (request.getScriptId() != null) {
            script = scriptRepository.findById(request.getScriptId())
                    .orElseGet(() -> {
                        String defaultCode = request.getScriptId() == 1L ? "ur"
                                : (request.getScriptId() == 2L ? "hi" : (request.getScriptId() == 3L ? "en" : null));
                        String defaultName = request.getScriptId() == 1L ? "Urdu"
                                : (request.getScriptId() == 2L ? "Hindi"
                                        : (request.getScriptId() == 3L ? "English" : null));
                        if (defaultCode != null) {
                            return scriptRepository.findByCode(defaultCode)
                                    .orElseGet(() -> scriptRepository.save(new ScriptEntity(defaultCode, defaultName,
                                            java.time.LocalDateTime.now(), java.time.LocalDateTime.now())));
                        }
                        throw new ResourceNotFoundException("Script not found with id: " + request.getScriptId());
                    });
        }

        // 3. Find target entity
        ContentTextEntity entity = null;

        // Case A: ID is provided
        if (request.getId() != null) {
            entity = contentTextRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("ContentText not found with id: " + request.getId()));
        }

        // Case B: Look up by (content_id, script_id) using resolved IDs to avoid duplicate key constraint violations
        Long resolvedContentId = content != null ? content.getId()
                : (entity != null && entity.getContent() != null ? entity.getContent().getId() : null);
        Long resolvedScriptId = script != null ? script.getId()
                : (entity != null && entity.getScript() != null ? entity.getScript().getId() : null);

        if (resolvedContentId != null && resolvedScriptId != null) {
            java.util.Optional<ContentTextEntity> existingOpt = contentTextRepository.findByContentIdAndScriptId(resolvedContentId, resolvedScriptId);
            if (existingOpt.isPresent()) {
                ContentTextEntity existing = existingOpt.get();
                // If entity was null (no id was provided) OR if an existing row already exists with this (content_id, script_id)
                if (entity == null || !existing.getId().equals(entity.getId())) {
                    entity = existing;
                }
            }
        }

        // Case C: Still null -> create new
        if (entity == null) {
            entity = new ContentTextEntity();
        }

        if (content != null) {
            entity.setContent(content);
        }
        if (script != null) {
            entity.setScript(script);
        }
        entity.setTitle(request.getTitle());
        entity.setBody(request.getBody());

        ContentTextEntity saved = contentTextRepository.save(entity);
        return mapToContentTextDto(saved);
    }

    public void deleteContentText(ContentTextDto request) {
        ContentTextEntity entity = contentTextRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ContentText not found with id: " + request.getId()));
        contentTextRepository.delete(entity);
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
