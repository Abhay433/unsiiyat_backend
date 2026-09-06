package com.unsiiyat.backend.modules.genre;

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
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Transactional
    public PagedResponse<GenreDto> filterGenre(GenreFilterRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<GenreEntity> page = genreRepository
                .findAll(GenreSpecification.filter(request), pageable);

        List<GenreDto> dtoList = page.getContent().stream().map(this::mapToGenreDto)
                .collect(Collectors.toList());

        Page<GenreDto> dtoPage = new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
        return PagedResponse.fromPage(dtoPage, "Genres fetched successfully");
    }

    @Transactional
    public void addOrUpdateGenre(GenreDto request) {

        if (request.getId() != null) {
            updateGenre(request);
        } else {
            createGenre(request);
        }

    }

    @Transactional
    public void createGenre(GenreDto request) {
        GenreEntity entity = new GenreEntity();
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        genreRepository.save(entity);
    }

    @Transactional
    public void updateGenre(GenreDto request) {
        GenreEntity entity = genreRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + request.getId()));
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        genreRepository.save(entity);
    }

    @Transactional
    public void deleteGenre(GenreDto request) {
        GenreEntity entity = genreRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + request.getId()));
        genreRepository.delete(entity);
    }

    public GenreDto mapToGenreDto(GenreEntity entity) {
        GenreDto dto = new GenreDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        return dto;
    }

}
