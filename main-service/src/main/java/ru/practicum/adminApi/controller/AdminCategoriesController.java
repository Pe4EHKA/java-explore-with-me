package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.service.category.AdminCategoriesService;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.NewCategoryDto;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoriesController {
    private final AdminCategoriesService adminCategoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("POST Admin Create new category: {}", newCategoryDto);
        return adminCategoriesService.createCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto,
                              @PathVariable(name = "catId") Long catId) {
        log.info("PATCH Admin Update category: {}", categoryDto);
        return adminCategoriesService.update(categoryDto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "catId") Long catId) {
        log.info("DELETE Admin Delete category: {}", catId);
        adminCategoriesService.deleteCategory(catId);
    }
}
