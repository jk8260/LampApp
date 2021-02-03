package com.elasalle.lamp.data.mapper;

public abstract class Cartographer<E,M> {
    public abstract E modelToEntity(M model);
    public abstract M entityToModel(E entity);
}
