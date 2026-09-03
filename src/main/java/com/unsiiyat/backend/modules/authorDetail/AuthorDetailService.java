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
import com.unsiiyat.backend.modules.script.ScriptEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class AuthorDetailService {

    @Autowired
    private AuthorDetailRepository authorDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
    public void addOrUpdateAuthorDetail(AuthorDetailDto request) {

        if (request.getId() != null) {
            updateAuthorDetail(request);
        } else {
            createAuthorDetail(request);
        }

    }

    @Transactional
    public void createAuthorDetail(AuthorDetailDto request) {
        AuthorDetailEntity entity = new AuthorDetailEntity();
        if (request.getAuthorId() != null) {
            entity.setAuthor(entityManager.getReference(AuthorEntity.class, request.getAuthorId()));
        }
        if (request.getScriptId() != null) {
            entity.setScript(entityManager.getReference(ScriptEntity.class, request.getScriptId()));
        }
        entity.setName(request.getName());
        entity.setBiography(request.getBiography());
        authorDetailRepository.save(entity);
    }

    @Transactional
    public void updateAuthorDetail(AuthorDetailDto request) {
        AuthorDetailEntity entity = authorDetailRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("AuthorDetail not found with id: " + request.getId()));
        if (request.getAuthorId() != null) {
            entity.setAuthor(entityManager.getReference(AuthorEntity.class, request.getAuthorId()));
        }
        if (request.getScriptId() != null) {
            entity.setScript(entityManager.getReference(ScriptEntity.class, request.getScriptId()));
        }
        entity.setName(request.getName());
        entity.setBiography(request.getBiography());
        authorDetailRepository.save(entity);
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
