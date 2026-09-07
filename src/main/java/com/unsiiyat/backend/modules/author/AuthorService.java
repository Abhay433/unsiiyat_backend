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

    @Autowired
    private com.unsiiyat.backend.modules.s3bucket.FileService fileService;

    @Transactional
    public PagedResponse<AuthorDto> filterAuthor(AuthorFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<AuthorEntity> page = authorRepository
                .findAll(AuthorSpecification.filter(request), pageable);

        Long preferredScriptId = request.getScriptId();
        if (preferredScriptId == null && request.getScriptCode() != null) {
            preferredScriptId = scriptRepository.findByCode(request.getScriptCode())
                    .map(ScriptEntity::getId)
                    .orElse(null);
        }

        final Long sId = preferredScriptId;
        List<AuthorDto> dtoList = page.getContent().stream()
                .map(author -> this.mapToAuthorDto(author, sId))
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

        saveAuthorDetails(saved, request);

        return mapToAuthorDto(saved);
    }

    @Transactional
    public AuthorEntity createAuthor(AuthorDto request) {
        AuthorEntity entity = new AuthorEntity();
        entity.setBirthDate(request.getBirthDate());
        entity.setDeathDate(request.getDeathDate());
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().trim().isEmpty()) {
            entity.setAvatarUrl(request.getAvatarUrl().trim());
        }
        entity.setAvatarUrl(request.getAvatarUrl());
        return authorRepository.save(entity);
    }

    @Transactional
    public AuthorEntity updateAuthor(AuthorDto request) {
        AuthorEntity entity = authorRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getId()));
        entity.setBirthDate(request.getBirthDate());
        entity.setDeathDate(request.getDeathDate());
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().trim().isEmpty()) {
            entity.setAvatarUrl(request.getAvatarUrl().trim());
        }
        return authorRepository.save(entity);
    }

    private void saveAuthorDetails(AuthorEntity author, AuthorDto request) {
        // 1. Process nested authorDetails or details list if provided
        List<AuthorDetailDto> detailList = request.getAuthorDetails();
        if (detailList == null || detailList.isEmpty()) {
            detailList = request.getDetails();
        }

        if (detailList != null && !detailList.isEmpty()) {
            for (AuthorDetailDto d : detailList) {
                if (d == null) continue;

                ScriptEntity script = resolveScript(d.getScriptId(), d.getName());
                if (script == null) continue;

                String name = d.getName() != null ? d.getName().trim() : "";
                String bio = d.getBiography() != null ? d.getBiography().trim() : "";

                if (name.isEmpty() && bio.isEmpty()) {
                    continue;
                }

                AuthorDetailEntity detail = authorDetailRepository
                        .findByAuthorIdAndScriptId(author.getId(), script.getId())
                        .orElseGet(() -> {
                            if (d.getId() != null) {
                                return authorDetailRepository.findById(d.getId()).orElseGet(AuthorDetailEntity::new);
                            }
                            return new AuthorDetailEntity();
                        });

                detail.setAuthor(author);
                detail.setScript(script);
                if (!name.isEmpty()) {
                    detail.setName(name);
                }
                detail.setBiography(bio);
                authorDetailRepository.save(detail);
            }
        }

        // 2. Process flat / convenience fields (urName, urBio, hiName, hiBio, enName, enBio)
        if ((request.getUrName() != null && !request.getUrName().trim().isEmpty())
                || (request.getUrBio() != null && !request.getUrBio().trim().isEmpty())) {
            saveOrUpdateDetail(author, "ur", "Urdu", request.getUrName(), request.getUrBio());
        }

        if ((request.getHiName() != null && !request.getHiName().trim().isEmpty())
                || (request.getHiBio() != null && !request.getHiBio().trim().isEmpty())) {
            saveOrUpdateDetail(author, "hi", "Hindi", request.getHiName(), request.getHiBio());
        }

        if ((request.getEnName() != null && !request.getEnName().trim().isEmpty())
                || (request.getEnBio() != null && !request.getEnBio().trim().isEmpty())) {
            saveOrUpdateDetail(author, "en", "English", request.getEnName(), request.getEnBio());
        } else if (request.getName() != null && !request.getName().trim().isEmpty()) {
            saveOrUpdateDetail(author, "en", "English", request.getName(), request.getPrimaryBio());
        } else if (request.getPrimaryName() != null && !request.getPrimaryName().trim().isEmpty()) {
            saveOrUpdateDetail(author, "en", "English", request.getPrimaryName(), request.getPrimaryBio());
        }
    }

    private void saveOrUpdateDetail(AuthorEntity author, String scriptCode, String scriptName, String name, String biography) {
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

        if (name != null && !name.trim().isEmpty()) {
            detail.setName(name.trim());
        }
        if (biography != null) {
            detail.setBiography(biography.trim());
        }
        authorDetailRepository.save(detail);
    }

    private ScriptEntity resolveScript(Long scriptId, String name) {
        if (scriptId != null) {
            return scriptRepository.findById(scriptId)
                    .orElseGet(() -> {
                        String defaultCode = scriptId == 1L ? "ur"
                                : (scriptId == 2L ? "hi" : (scriptId == 3L ? "en" : null));
                        String defaultName = scriptId == 1L ? "Urdu"
                                : (scriptId == 2L ? "Hindi" : (scriptId == 3L ? "English" : null));
                        if (defaultCode != null) {
                            return scriptRepository.findByCode(defaultCode)
                                    .orElseGet(() -> scriptRepository.save(new ScriptEntity(defaultCode, defaultName,
                                            LocalDateTime.now(), LocalDateTime.now())));
                        }
                        return null;
                    });
        }
        if (name != null && !name.trim().isEmpty()) {
            String detectedCode = com.unsiiyat.backend.common.util.LanguageDetectorUtil.detectLanguageCode(name);
            if (detectedCode != null && !"unknown".equalsIgnoreCase(detectedCode)) {
                return scriptRepository.findByCode(detectedCode).orElse(null);
            }
        }
        return null;
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

    @Transactional
    public String updateAuthorPhoto(Long authorId, org.springframework.web.multipart.MultipartFile file) {
        AuthorEntity author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        String oldPhotoUrl = author.getAvatarUrl();
        com.unsiiyat.backend.modules.s3bucket.UploadedFileDto uploaded = fileService.uploadAndSave(file);
        author.setAvatarUrl(uploaded.getFilePath());
        authorRepository.save(author);
        if (oldPhotoUrl != null && !oldPhotoUrl.isBlank()) {
            try {
                fileService.deleteFileByPath(oldPhotoUrl);
            } catch (Exception e) {
                // ignore error if previous file could not be deleted
            }
        }
        return uploaded.getFilePath();
    }

    public AuthorDto mapToAuthorDto(AuthorEntity entity) {
        return mapToAuthorDto(entity, null);
    }

    public AuthorDto mapToAuthorDto(AuthorEntity entity, Long preferredScriptId) {
        if (entity == null) {
            return null;
        }
        AuthorDto dto = new AuthorDto();
        dto.setId(entity.getId());
        dto.setAvatarUrl(entity.getAvatarUrl());
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
            AuthorDetailDto preferredDetail = null;

            for (AuthorDetailEntity d : details) {
                AuthorDetailDto dDto = new AuthorDetailDto();
                dDto.setId(d.getId());
                dDto.setAuthorId(entity.getId());
                if (d.getScript() != null) {
                    Long sId = d.getScript().getId();
                    dDto.setScriptId(sId);
                    String code = d.getScript().getCode();
                    if ("ur".equalsIgnoreCase(code)) {
                        dto.setUrName(d.getName());
                        dto.setUrBio(d.getBiography());
                    } else if ("hi".equalsIgnoreCase(code)) {
                        dto.setHiName(d.getName());
                        dto.setHiBio(d.getBiography());
                    } else if ("en".equalsIgnoreCase(code)) {
                        dto.setEnName(d.getName());
                        dto.setEnBio(d.getBiography());
                    }
                    if (preferredScriptId != null && sId.equals(preferredScriptId)) {
                        preferredDetail = dDto;
                    }
                }
                dDto.setName(d.getName());
                dDto.setBiography(d.getBiography());
                detailDtos.add(dDto);
            }
            dto.setDetails(detailDtos);
            dto.setAuthorDetails(detailDtos);

            AuthorDetailDto active = preferredDetail;
            if (active == null) {
                active = detailDtos.stream()
                        .filter(d -> dto.getEnName() != null && dto.getEnName().equals(d.getName()))
                        .findFirst()
                        .orElseGet(() -> detailDtos.stream()
                                .filter(d -> dto.getUrName() != null && dto.getUrName().equals(d.getName()))
                                .findFirst()
                                .orElse(detailDtos.get(0)));
            }

            dto.setPrimaryName(active.getName());
            dto.setName(active.getName());
            dto.setPrimaryBio(active.getBiography());
        }

        return dto;
    }

}
