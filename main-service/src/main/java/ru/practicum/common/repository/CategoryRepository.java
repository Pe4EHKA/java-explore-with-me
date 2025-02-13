package ru.practicum.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByName(String name);
}
