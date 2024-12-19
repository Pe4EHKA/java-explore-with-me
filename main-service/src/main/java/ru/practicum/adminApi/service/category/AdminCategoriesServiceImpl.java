package ru.practicum.adminApi.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCategoriesServiceImpl implements AdminCategoriesService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

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

        if (!categoryOld.getName().equalsIgnoreCase(categoryDto.getName())) {
            if (categoryRepository.existsByName(categoryDto.getName())) {
                throw new ConflictException(String.format("Category with name '%s' already exists", categoryDto.getName()));
            }
        }

        Category newCategory = CategoryMapper.toCategory(categoryDto);
        newCategory.setId(categoryOld.getId());

        log.info("Category updated: {}", newCategory);

        return CategoryMapper.toCategoryDto(categoryRepository.save(newCategory));

    }

    @Override
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConditionsNotMetException(String.format("Category with id: %d still in use with event", catId));
        } else {
            log.info("Category with id: {} deleted", catId);
            categoryRepository.deleteById(catId);
        }
    }
}
