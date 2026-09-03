package com.unsiiyat.backend.modules.author;

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
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Transactional
    public PagedResponse<AuthorDto> filterAuthor(AuthorFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<AuthorEntity> page = authorRepository
                .findAll(AuthorSpecification.filter(request), pageable);

        List<AuthorDto> dtoList = page.getContent().stream().map(this::mapToAuthorDto)
                .collect(Collectors.toList());

        Page<AuthorDto> dtoPage = new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
        return PagedResponse.fromPage(dtoPage, "Authors fetched successfully");
    }

    @Transactional
    public void addOrUpdateAuthor(AuthorDto request) {

        if (request.getId() != null) {
            updateAuthor(request);
        } else {
            createAuthor(request);
        }

    }

    @Transactional
    public void createAuthor(AuthorDto request) {
        AuthorEntity entity = new AuthorEntity();
        entity.setBirthDate(request.getBirthDate());
        entity.setDeathDate(request.getDeathDate());
        authorRepository.save(entity);
    }

    @Transactional
    public void updateAuthor(AuthorDto request) {
        AuthorEntity entity = authorRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getId()));
        entity.setBirthDate(request.getBirthDate());
        entity.setDeathDate(request.getDeathDate());
        authorRepository.save(entity);
    }

    public AuthorDto mapToAuthorDto(AuthorEntity entity) {
        AuthorDto dto = new AuthorDto();
        dto.setId(entity.getId());
        dto.setBirthDate(entity.getBirthDate());
        dto.setDeathDate(entity.getDeathDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

}
