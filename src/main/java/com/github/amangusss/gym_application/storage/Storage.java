package com.github.amangusss.gym_application.storage;

public interface Storage<T> {
    T save(T entity);
    T findById(Long id);
    Iterable<T> findAll();
    T update(T entity);
    boolean deleteById(Long id);
    boolean existsById(Long id);
    Long generateNextId();
}
