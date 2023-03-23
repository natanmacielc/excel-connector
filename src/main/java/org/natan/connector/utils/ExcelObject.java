package org.natan.connector.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.text.MessageFormat.format;

public class ExcelObject extends ReflectedObject {

    public ExcelObject(Class<?> clasz) {
        super(clasz);
    }

    public int getCellNumber(String fieldName) {
        Field field = getField(fieldName);
        if (!field.isAnnotationPresent(ExcelColumn.class)) {
            throw new IllegalArgumentException(format("Campo {0} não tem a anotação ExcelColumn", fieldName));
        }
        return field.getDeclaredAnnotation(ExcelColumn.class).cell();
    }

    private void removeUnannotatedFields(List<Field> fields) {
        fields.removeIf(field -> !field.isAnnotationPresent(ExcelColumn.class));
    }

    private Field getField(String fieldName) {
        return getFields(super.getType())
                .stream()
                .filter(field -> field.getName().equals(fieldName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(format("Campo {0} não encontrado na classe {1} ", fieldName, super.getType().getSimpleName())));
    }

    @Override
    protected List<Field> getFields(Class<?> clasz) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clasz.getDeclaredFields()));
        removeUnannotatedFields(fields);
        return fields;
    }
}
