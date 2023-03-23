package org.natan.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TypeCaster {
    public static <T> T cast(Object obj, Class<T> clazz) {
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        } else {
            throw new ClassCastException(MessageFormat.format("Object cannot be cast to {0}", clazz.getName()));
        }
    }

    public static <T> List<T> castList(List<?> list, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (Object o : list) {
            if (clazz.isInstance(o)) {
                result.add(clazz.cast(o));
            } else {
                throw new ClassCastException(MessageFormat.format("Cannot cast list element {0} to {1}", o.getClass(), clazz));
            }
        }
        return result;
    }
}
