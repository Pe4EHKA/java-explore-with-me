package ru.practicum.common.mapper;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class PropertyMerger {

    public <T> void mergeProperties(final T source, final T target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target objects must not be null");
        }

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        for (Field sourceField : sourceClass.getDeclaredFields()) {
            sourceField.setAccessible(true);
            try {
                Field targetField = getFieldIfExists(targetClass, sourceField.getName());
                if (targetField != null) {
                    targetField.setAccessible(true);
                    Object value = sourceField.get(source);
                    if (value != null) {
                        targetField.set(target, value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to copy property " + sourceField.getName(), e);
            }
        }
    }

    private Field getFieldIfExists(Class<?> targetClass, String fieldName) {
        try {
            return targetClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
