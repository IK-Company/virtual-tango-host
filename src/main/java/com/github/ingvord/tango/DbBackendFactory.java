package com.github.ingvord.tango;

import java.lang.reflect.InvocationTargetException;

public class DbBackendFactory {
    private static final DbBackendFactory INSTANCE = new DbBackendFactory();

    private Class<? extends DbBackend> clzzz = InMemoryDbBackend.class;

    private DbBackendFactory(){}

    public static DbBackendFactory getInstance(){
        return INSTANCE;
    }

    public void setDbBackendImplementationClass(Class<? extends DbBackend> clzzz){
        this.clzzz = clzzz;
    }

    public DbBackend createInstance(){
        try {
            return clzzz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException| IllegalAccessException| InvocationTargetException| NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
