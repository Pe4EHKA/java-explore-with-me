package ru.practicum.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.NewCategoryDto;
import ru.practicum.common.exception.ConditionsNotMetException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.CategoryMapper;
import ru.practicum.common.model.Category;
import ru.practicum.common.repository.CategoryRepository;
import ru.practicum.common.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;


    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));

        List<Category> categories = categoryRepository.findAll(pageRequest).toList();

        log.info("Got {} categories", categories.size());
        return CategoryMapper.toCategotyDtoList(categories);
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id: " + catId + " not found"));
        log.info("Got category: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);

        try {
            category = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Category created: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto categoryDto, Long catId) {
        Category categoryOld = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id: %d not found", catId)));


        Category newCategory = CategoryMapper.toCategory(categoryDto);
        categoryOld.setName(newCategory.getName());

        try {
            categoryRepository.save(categoryOld);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("Category with name '%s' already exists", categoryDto.getName()));
        }

        log.info("Category updated: {}", categoryOld);

        return CategoryMapper.toCategoryDto(categoryOld);

    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConditionsNotMetException(String.format("Category with id: %d still in use with event", catId));
        } else {
            log.info("Category with id: {} deleted", catId);
            categoryRepository.deleteById(catId);
            categoryRepository.flush();
        }
    }
}
