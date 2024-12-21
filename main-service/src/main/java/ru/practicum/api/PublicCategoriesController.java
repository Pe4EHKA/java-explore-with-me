package ru.practicum.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoriesController {

    private final CategoryService publicCategoryService;


    @GetMapping
    List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("PUBLIC Get categories with from {}, size {}", from, size);
        return publicCategoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getCategory(@PathVariable("catId") Long catId) {
        log.info("PUBLIC Get category with id {}", catId);
        return publicCategoryService.getCategory(catId);
    }
}
