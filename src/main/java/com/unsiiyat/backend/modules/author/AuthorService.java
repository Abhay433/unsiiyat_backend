package com.unsiiyat.backend.modules.author;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.unsiiyat.backend.modules.authorDetail.AuthorDetailDto;
import com.unsiiyat.backend.modules.authorDetail.AuthorDetailEntity;
import com.unsiiyat.backend.modules.authorDetail.AuthorDetailRepository;
import com.unsiiyat.backend.modules.content.ContentEntity;
import com.unsiiyat.backend.modules.content.ContentRepository;
import com.unsiiyat.backend.modules.script.ScriptEntity;
import com.unsiiyat.backend.modules.script.ScriptRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorDetailRepository authorDetailRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private ContentRepository contentRepository;

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
    public AuthorDto addOrUpdateAuthor(AuthorDto request) {
        AuthorEntity saved;
        if (request.getId() != null) {
            saved = updateAuthor(request);
        } else {
            saved = createAuthor(request);
        }

        saveAuthorNameDetails(saved, request);

        return mapToAuthorDto(saved);
    }

    @Transactional
    public AuthorEntity createAuthor(AuthorDto request) {
        AuthorEntity entity = new AuthorEntity();
        entity.setBirthDate(request.getBirthDate());
        entity.setDeathDate(request.getDeathDate());
        return authorRepository.save(entity);
    }

    @Transactional
    public AuthorEntity updateAuthor(AuthorDto request) {
        AuthorEntity entity = authorRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getId()));
        entity.setBirthDate(request.getBirthDate());
        entity.setDeathDate(request.getDeathDate());
        return authorRepository.save(entity);
    }

    private void saveAuthorNameDetails(AuthorEntity author, AuthorDto request) {
        if (request.getUrName() != null && !request.getUrName().trim().isEmpty()) {
            saveOrUpdateDetail(author, "ur", "Urdu", request.getUrName().trim());
        }
        if (request.getHiName() != null && !request.getHiName().trim().isEmpty()) {
            saveOrUpdateDetail(author, "hi", "Hindi", request.getHiName().trim());
        }
        if (request.getEnName() != null && !request.getEnName().trim().isEmpty()) {
            saveOrUpdateDetail(author, "en", "English", request.getEnName().trim());
        } else if (request.getName() != null && !request.getName().trim().isEmpty()) {
            saveOrUpdateDetail(author, "en", "English", request.getName().trim());
        } else if (request.getPrimaryName() != null && !request.getPrimaryName().trim().isEmpty()) {
            saveOrUpdateDetail(author, "en", "English", request.getPrimaryName().trim());
        }

        if (request.getDetails() != null) {
            for (AuthorDetailDto d : request.getDetails()) {
                if (d.getName() != null && !d.getName().trim().isEmpty() && d.getScriptId() != null) {
                    ScriptEntity script = scriptRepository.findById(d.getScriptId()).orElse(null);
                    if (script != null) {
                        saveOrUpdateDetail(author, script.getCode(), script.getName(), d.getName().trim());
                    }
                }
            }
        }
    }

    private void saveOrUpdateDetail(AuthorEntity author, String scriptCode, String scriptName, String name) {
        ScriptEntity script = scriptRepository.findByCode(scriptCode)
                .orElseGet(() -> scriptRepository
                        .save(new ScriptEntity(scriptCode, scriptName, LocalDateTime.now(), LocalDateTime.now())));

        AuthorDetailEntity detail = authorDetailRepository.findByAuthorIdAndScriptId(author.getId(), script.getId())
                .orElseGet(() -> {
                    AuthorDetailEntity d = new AuthorDetailEntity();
                    d.setAuthor(author);
                    d.setScript(script);
                    return d;
                });
        detail.setName(name);
        authorDetailRepository.save(detail);
    }

    @Transactional
    public void deleteAuthor(AuthorDto request) {
        AuthorEntity entity = authorRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getId()));

        List<ContentEntity> contents = contentRepository.findByAuthorId(entity.getId());
        if (contents != null && !contents.isEmpty()) {
            contentRepository.deleteAll(contents);
        }

        authorRepository.delete(entity);
    }

    public AuthorDto mapToAuthorDto(AuthorEntity entity) {
        if (entity == null) {
            return null;
        }
        AuthorDto dto = new AuthorDto();
        dto.setId(entity.getId());
        dto.setBirthDate(entity.getBirthDate());
        dto.setDeathDate(entity.getDeathDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<AuthorDetailEntity> details = entity.getAuthorDetails();
        if (details == null || details.isEmpty()) {
            details = authorDetailRepository.findByAuthorId(entity.getId());
        }

        if (details != null && !details.isEmpty()) {
            List<AuthorDetailDto> detailDtos = new ArrayList<>();
            for (AuthorDetailEntity d : details) {
                AuthorDetailDto dDto = new AuthorDetailDto();
                dDto.setId(d.getId());
                dDto.setAuthorId(entity.getId());
                if (d.getScript() != null) {
                    dDto.setScriptId(d.getScript().getId());
                    String code = d.getScript().getCode();
                    if ("ur".equalsIgnoreCase(code)) {
                        dto.setUrName(d.getName());
                    } else if ("hi".equalsIgnoreCase(code)) {
                        dto.setHiName(d.getName());
                    } else if ("en".equalsIgnoreCase(code)) {
                        dto.setEnName(d.getName());
                    }
                }
                dDto.setName(d.getName());
                dDto.setBiography(d.getBiography());
                detailDtos.add(dDto);
            }
            dto.setDetails(detailDtos);
            dto.setAuthorDetails(detailDtos);

            String primary = dto.getPrimaryName() != null ? dto.getPrimaryName()
                    : (dto.getEnName() != null ? dto.getEnName()
                            : (dto.getUrName() != null ? dto.getUrName()
                                    : (dto.getHiName() != null ? dto.getHiName() : detailDtos.get(0).getName())));
            dto.setPrimaryName(primary);
            dto.setName(primary);
        }

        return dto;
    }

}
