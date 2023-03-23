package org.natan.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionUtils {
    @SafeVarargs
    public static List<List<Object>> genericList(List<Object>... objects) {
        List<List<Object>> genericList = new ArrayList<>();
        Collections.addAll(genericList, objects);
        return genericList;
    }

    public static <T> List<T> findListByType(List<List<Object>> list, Class<T> type) {
        for (List<Object> innerList : list) {
            if (!innerList.isEmpty()) {
                Object obj = innerList.get(0);
                if (obj != null && obj.getClass().equals(type)) {
                    return TypeCaster.castList(innerList, type);
                }
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("Não existe lista para a classe {0}", type.getSimpleName()));
    }
}
