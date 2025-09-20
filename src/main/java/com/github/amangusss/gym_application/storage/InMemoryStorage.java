package com.github.amangusss.gym_application.storage;

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
            throw new IllegalArgumentException("Entity must not be null");
        }

        Long id = extractId(entity);
        if (id == null) {
            id = generateNextId();
            setId(entity, id);
        }

        storage.put(id, entity);
        logger.debug("Saved entity with id: {}", id);
        return entity;
    }

    @Override
    public T findById(Long id) {
        if (id == null) {
            return null;
        }

        T entity = storage.get(id);
        logger.debug("Find by id {}: {}", id, entity != null ? "found" : "not found");
        return storage.get(id);
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>(storage.values());
        logger.debug("Find all: {}", result.size());
        return result;
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        Long id = extractId(entity);
        if (id == null || !storage.containsKey(id)) {
            throw new IllegalArgumentException("Entity with id " + id + " not found");
        }

        storage.put(id, entity);
        logger.debug("Updated entity with id: {}", id);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }

        boolean removed = storage.remove(id) != null;
        logger.debug("Deleted entity with id {} : {} ", id, removed ? "success" : "failed");
        return removed;
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public void clear() {
        int size = storage.size();
        storage.clear();
        logger.debug("Cleared {} entities", size);
    }

    @Override
    public Long generateNextId() {
        return nextId++;
    }
}