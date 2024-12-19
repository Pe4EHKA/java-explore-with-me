package ru.practicum.adminApi.service.category;

import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.NewCategoryDto;

public interface AdminCategoriesService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto update(CategoryDto categoryDto, Long catId);

    void deleteCategory(Long catId);
}
