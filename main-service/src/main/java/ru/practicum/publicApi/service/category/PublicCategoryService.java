package ru.practicum.publicApi.service.category;

import ru.practicum.common.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategory(Long catId);
}
