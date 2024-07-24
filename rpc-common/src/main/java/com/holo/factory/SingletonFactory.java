package com.holo.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description: 生成单例对象的工厂，给需要单例对象的类使用
 */
public class SingletonFactory {

    private static final Map<String,Object> beanFactory;
    static {
        beanFactory = new ConcurrentHashMap<>();
    }

    private SingletonFactory() {
    }
    public static <T> T getInstance(Class<T> clazz){
        if(clazz == null){
            throw new IllegalArgumentException();
        }

        String key = clazz.getName();
        if(beanFactory.containsKey(key)){
            return clazz.cast(beanFactory.get(key));
        }else {
            return clazz.cast(beanFactory.computeIfAbsent(key,k->{
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
            }));
        }
    }
}
