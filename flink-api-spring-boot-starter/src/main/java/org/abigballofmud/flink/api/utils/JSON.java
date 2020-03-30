package org.abigballofmud.flink.api.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.abigballofmud.flink.api.exceptions.FlinkCommonException;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 21:09
 * @since 1.0
 */
public class JSON {

    private JSON() throws IllegalAccessException {
        throw new IllegalAccessException("util class");
    }

    private static ObjectMapper objectMapper;

    static {
        objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
    }

    public static <T> T toObj(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new FlinkCommonException("error.jackson.read", e);
        }
    }

    public static <T> List<T> toArray(String json, Class<T> clazz) {
        try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new FlinkCommonException("error.jackson.read", e);
        }
    }

    public static <T> String toJson(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new FlinkCommonException("error.jackson.write", e);
        }
    }

}
