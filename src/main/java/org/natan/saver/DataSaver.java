package org.natan.saver;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public abstract class DataSaver {
    public void saveAll(List<List<Object>> objects, JpaRepository... repositories) {
        for (JpaRepository repository : repositories) {
            try {
                repository.saveAll(getObjects(objects, getEntityType(repository)));
            } catch (DataAccessException e) {
                throw new RuntimeException("Erro ao salvar objetos no repositório " + repository.getClass().getSimpleName(), e);
            }
        }
    }

    private List<Object> getObjects(List<List<Object>> objects, Class<?> type) {
        return objects.stream()
                .filter(list -> list != null && !list.isEmpty() && list.get(0).getClass() == type)
                .findFirst()
                .orElse(Collections.emptyList());
    }

    private Class<?> getEntityType(JpaRepository repository) throws IllegalStateException {
        try {
            Class<?> repositoryClass = Class.forName(repository.getClass().getGenericInterfaces()[0].getTypeName());
            Type[] genericInterfaces = repositoryClass.getGenericInterfaces();
            if (genericInterfaces.length == 0) {
                throw new IllegalStateException("Não foi possível obter informações sobre o tipo de entidade do repositório " + repositoryClass.getSimpleName());
            }
            Type genericInterface = genericInterfaces[0];
            if (!(genericInterface instanceof ParameterizedType)) {
                throw new IllegalStateException("Não foi possível obter informações sobre o tipo de entidade do repositório " + repositoryClass.getSimpleName());
            }
            ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments == null || actualTypeArguments.length == 0) {
                throw new IllegalStateException("Não foi possível obter informações sobre o tipo de entidade do repositório " + repositoryClass.getSimpleName());
            }
            Type actualTypeArgument = actualTypeArguments[0];
            if (!(actualTypeArgument instanceof Class)) {
                throw new IllegalStateException("Não foi possível obter informações sobre o tipo de entidade do repositório " + repositoryClass.getSimpleName());
            }
            return (Class<?>) actualTypeArgument;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
