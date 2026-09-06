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
        ContentTextEntity entity;
        if (request.getId() != null) {
            entity = contentTextRepository.findById(request.getId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("ContentText not found with id: " + request.getId()));
        } else if (request.getContentId() != null && request.getScriptId() != null) {
            entity = contentTextRepository.findByContentIdAndScriptId(request.getContentId(), request.getScriptId())
                    .orElseGet(ContentTextEntity::new);
        } else {
            entity = new ContentTextEntity();
        }

        if (request.getContentId() != null) {
            ContentEntity content = contentRepository.findById(request.getContentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: "
                            + request.getContentId() + ". Please create the content record first."));
            entity.setContent(content);
        }
        if (request.getScriptId() != null) {
            ScriptEntity script = scriptRepository.findById(request.getScriptId())
                    .orElseGet(() -> {
                        // Fallback: check or create standard script by code if ID 1, 2, or 3 doesn't
                        // exist yet
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
