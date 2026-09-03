package com.unsiiyat.backend.modules.script;

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

import jakarta.transaction.Transactional;

@Service
public class ScriptService {

    @Autowired
    private ScriptRepository scriptRepository;

    @Transactional
    public PagedResponse<ScriptDto> filterScript(ScriptFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<ScriptEntity> page = scriptRepository
                .findAll(ScriptSpecification.filter(request), pageable);

        List<ScriptDto> dtoList = page.getContent().stream().map(this::mapToScriptDto)
                .collect(Collectors.toList());

        Page<ScriptDto> dtoPage = new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
        return PagedResponse.fromPage(dtoPage, "Scripts fetched successfully");
    }

    @Transactional
    public void addOrUpdateScript(ScriptDto request) {

        if (request.getId() != null) {
            updateScript(request);
        } else {
            createScript(request);
        }

    }

    @Transactional
    public void createScript(ScriptDto request) {
        ScriptEntity entity = new ScriptEntity();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        scriptRepository.save(entity);
    }

    @Transactional
    public void updateScript(ScriptDto request) {
        ScriptEntity entity = scriptRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Script not found with id: " + request.getId()));
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        scriptRepository.save(entity);
    }

    public ScriptDto mapToScriptDto(ScriptEntity entity) {
        ScriptDto dto = new ScriptDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

}
