package com.taipicala.dao;

import java.util.List;

public interface GenericDAO<T, ID> {
    T create(T t);
    T read(ID id);
    T update(T t);
    void delete(T t);
    List<T> readAll();
}
