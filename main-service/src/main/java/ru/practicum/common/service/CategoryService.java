package ru.practicum.common.service;

import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategory(Long catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto update(CategoryDto categoryDto, Long catId);

    void deleteCategory(Long catId);
}
