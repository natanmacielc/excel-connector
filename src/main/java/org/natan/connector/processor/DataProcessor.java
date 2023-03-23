package org.natan.connector.processor;

import java.util.List;

public interface DataProcessor {
    void databaseInitializerProcessor(List<List<Object>> objects);
}
