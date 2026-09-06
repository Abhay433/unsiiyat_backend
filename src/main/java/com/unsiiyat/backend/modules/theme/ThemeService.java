package com.unsiiyat.backend.modules.theme;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.unsiiyat.backend.common.exceptions.ResourceNotFoundException;
import com.unsiiyat.backend.common.response.PagedResponse;

import jakarta.transaction.Transactional;

@Service
public class ThemeService {

    @Autowired
    private ThemeRepository themeRepository;

    @Transactional
    public PagedResponse<ThemeDto> filterTheme(ThemeFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<ThemeEntity> page = themeRepository
                .findAll(ThemeSpecification.filter(request), pageable);

        List<ThemeDto> dtoList = page.getContent().stream().map(this::mapToThemeDto)
                .collect(Collectors.toList());

        Page<ThemeDto> dtoPage = new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
        return PagedResponse.fromPage(dtoPage, "Student marks fetched successfully");
    }

    @Transactional
    public void addOrUpdateTheme(ThemeDto request) {

        if (request.getId() != null) {
            updateTheme(request);
        } else {
            createTheme(request);
        }

    }

    @Transactional
    public void createTheme(ThemeDto request) {
        ThemeEntity entity = new ThemeEntity();
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        themeRepository.save(entity);
    }

    @Transactional
    public void updateTheme(ThemeDto request) {
        ThemeEntity entity = themeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + request.getId()));
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        themeRepository.save(entity);
    }

    @Transactional
    public void deleteTheme(ThemeDto request) {
        ThemeEntity entity = themeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + request.getId()));
        themeRepository.delete(entity);
    }

    public ThemeDto mapToThemeDto(ThemeEntity entity) {
        ThemeDto dto = new ThemeDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        return dto;
    }

}
