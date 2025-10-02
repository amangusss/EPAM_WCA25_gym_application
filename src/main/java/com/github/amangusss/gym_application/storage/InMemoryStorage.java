package com.github.amangusss.gym_application.storage;

import com.github.amangusss.gym_application.exception.ValidationException;
import com.github.amangusss.gym_application.util.constants.LoggerConstants;
import com.github.amangusss.gym_application.util.constants.ValidationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InMemoryStorage<T> implements Storage<T> {

    protected final Map<Long, T> storage = new HashMap<>();
    protected Long nextId = 1L;

    public static final Logger logger = LoggerFactory.getLogger(InMemoryStorage.class);

    protected abstract Long extractId(T entity);
    protected abstract void setId(T entity, Long id);

    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new ValidationException(ValidationConstants.ENTITY_MUST_NOT_BE_NULL);
        }

        Long id = extractId(entity);
        if (id == null) {
            id = generateNextId();
            setId(entity, id);
        }

        storage.put(id, entity);
        logger.debug(LoggerConstants.ENTITY_SAVED, id);
        return entity;
    }

    @Override
    public T findById(Long id) {
        if (id == null) {
            return null;
        }

        T entity = storage.get(id);
        logger.debug(LoggerConstants.ENTITY_FOUND, id, entity != null ? "found" : "not found");
        return storage.get(id);
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>(storage.values());
        logger.debug(LoggerConstants.ENTITY_FIND_ALL, result.size());
        return result;
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new ValidationException(ValidationConstants.ENTITY_MUST_NOT_BE_NULL);
        }

        Long id = extractId(entity);
        if (id == null || !storage.containsKey(id)) {
            throw new ValidationException(String.format(ValidationConstants.ENTITY_NOT_FOUND_BY_ID, id));
        }

        storage.put(id, entity);
        logger.debug(LoggerConstants.ENTITY_UPDATED, id);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }

        boolean removed = storage.remove(id) != null;
        logger.debug(LoggerConstants.ENTITY_DELETED, id, removed ? "success" : "failed");
        return removed;
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && storage.containsKey(id);
    }


    @Override
    public Long generateNextId() {
        return nextId++;
    }
}