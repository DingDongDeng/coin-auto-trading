package com.dingdongdeng.coinautotrading.common.domain;

import java.util.List;

public interface RepositoryService<T, ID> {

    T findById(ID id);

    T save(T entity);

    List<T> saveAll(Iterable<T> iterable);
}
