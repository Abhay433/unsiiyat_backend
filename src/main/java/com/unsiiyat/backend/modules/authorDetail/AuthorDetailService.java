package com.unsiiyat.backend.modules.authorDetail;

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
import com.unsiiyat.backend.modules.author.AuthorEntity;
import com.unsiiyat.backend.modules.author.AuthorRepository;
import com.unsiiyat.backend.modules.script.ScriptEntity;
import com.unsiiyat.backend.modules.script.ScriptRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthorDetailService {

    @Autowired
    private AuthorDetailRepository authorDetailRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Transactional
    public PagedResponse<AuthorDetailDto> filterAuthorDetail(AuthorDetailFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<AuthorDetailEntity> page = authorDetailRepository
                .findAll(AuthorDetailSpecification.filter(request), pageable);

        List<AuthorDetailDto> dtoList = page.getContent().stream().map(this::mapToAuthorDetailDto)
                .collect(Collectors.toList());

        Page<AuthorDetailDto> dtoPage = new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
        return PagedResponse.fromPage(dtoPage, "Author details fetched successfully");
    }

    @Transactional
    public AuthorDetailDto addOrUpdateAuthorDetail(AuthorDetailDto request) {
        AuthorDetailEntity entity;
        if (request.getId() != null) {
            entity = authorDetailRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("AuthorDetail not found with id: " + request.getId()));
        } else if (request.getAuthorId() != null && request.getScriptId() != null) {
            entity = authorDetailRepository.findByAuthorIdAndScriptId(request.getAuthorId(), request.getScriptId())
                    .orElseGet(AuthorDetailEntity::new);
        } else {
            entity = new AuthorDetailEntity();
        }

        if (request.getAuthorId() != null) {
            AuthorEntity author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));
            entity.setAuthor(author);
        }
        if (request.getScriptId() != null) {
            ScriptEntity script = scriptRepository.findById(request.getScriptId())
                    .orElseThrow(() -> new ResourceNotFoundException("Script not found with id: " + request.getScriptId()));
            entity.setScript(script);
        }
        entity.setName(request.getName());
        entity.setBiography(request.getBiography());

        AuthorDetailEntity saved = authorDetailRepository.save(entity);
        return mapToAuthorDetailDto(saved);
    }

    public AuthorDetailDto mapToAuthorDetailDto(AuthorDetailEntity entity) {
        AuthorDetailDto dto = new AuthorDetailDto();
        dto.setId(entity.getId());
        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
        }
        if (entity.getScript() != null) {
            dto.setScriptId(entity.getScript().getId());
        }
        dto.setName(entity.getName());
        dto.setBiography(entity.getBiography());
        return dto;
    }

}
