package org.natan.processor;

import java.util.List;

public interface DataProcessor {
    void databaseInitializerProcessor(List<List<Object>> objects);
}
