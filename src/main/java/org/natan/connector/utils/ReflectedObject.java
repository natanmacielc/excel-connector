package org.natan.connector.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.text.MessageFormat.format;

@CommonsLog
public class ReflectedObject {
    private final Map<String, Field> fields;
    private final Map<String, Object> values;
    @Getter(AccessLevel.PROTECTED)
    private final Class<?> type;

    public ReflectedObject(Object object) {
        this.type = object.getClass();
        this.fields = Collections.unmodifiableMap(mapFields(this.type));
        this.values = Collections.unmodifiableMap(mapValues(object));
    }

    public ReflectedObject(final Class<?> clasz) {
        this.type = clasz;
        this.fields = Collections.unmodifiableMap(mapFields(clasz));
        this.values = Collections.emptyMap();
    }

    public <T> T getFieldValue(String fieldName, Class<T> type) {
        Object object = getFieldValue(fieldName);
        if (object == null) {
            return null;
        }
        if (type.isAssignableFrom(object.getClass())) {
            return TypeCaster.cast(object, type);
        }
        return null;
    }

    public Object getFieldValue(String fieldName) {
        return values.get(fieldName);
    }

    public boolean containsField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    public boolean containsFields(String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (!containsField(fieldName)) {
                return false;
            }
        }
        return true;
    }

    public Class<?> getFieldType(String fieldName) {
        Field field = fields.get(fieldName);
        if (field == null) {
            return null;
        }
        Class<?> fieldType = field.getType();
        if (fieldType == List.class) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            fieldType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }

        return fieldType;
    }

    public Set<String> getFieldNames() {
        return Collections.unmodifiableSet(fields.keySet());
    }

    private Map<String, Object> mapValues(Object object) {
        final Class<?> clasz = object.getClass();
        Map<String, Object> map = new HashMap<>();
        Collection<Field> fields = this.fields.values();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                log.warn(format("Erro ao obter valor do campo {0} na classe {1}: {2}", field.getName(), clasz.getSimpleName(), e.getMessage()));
            }
            map.put(field.getName(), value);
        }
        return map;
    }

    private Map<String, Field> mapFields(Class<?> clasz) {
        final List<Field> fieldList = getFields(clasz);
        final Class<?> superclass = clasz.getSuperclass();
        Map<String, Field> map = new HashMap<>();
        if (superClassExists(superclass)) {
            fieldList.addAll(Arrays.asList(superclass.getDeclaredFields()));
            Class<?> superSuperClass = superclass.getSuperclass();
            if (superClassExists(superSuperClass)) {
                fieldList.addAll(Arrays.asList(superSuperClass.getDeclaredFields()));
                Class<?> superSuperSuperClass = superSuperClass.getSuperclass();
                if (superClassExists(superSuperSuperClass)) {
                    throw new IllegalArgumentException(format("Classe {0} tem mais do que 2 pais", clasz.getSimpleName()));
                }
            }
        }
        for (Field field : fieldList) {
            if (map.containsKey(field.getName())) {
                log.error(format("Campo {0} duplicado na classe {1}. Corrigir.", field.getName(), clasz.getSimpleName()));
            } else if (!Modifier.isTransient(field.getModifiers())) {
                map.put(field.getName(), field);
            }
        }
        return map;
    }

    private static boolean superClassExists(Class<?> superClass) {
        return superClass != null && !superClass.equals(Object.class);
    }

    public boolean isAnnotatedWith(String fieldName, Class<? extends Annotation> type) {
        Field field = fields.get(fieldName);
        if (field == null) {
            return false;
        }
        return field.isAnnotationPresent(type);
    }

    protected List<Field> getFields(Class<?> clasz) {
        return new ArrayList<>(Arrays.asList(clasz.getDeclaredFields()));
    }
}
