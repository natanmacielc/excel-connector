package org.natan.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.natan.utils.ExcelObject;
import org.natan.utils.ExcelSheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ExcelReader<T> {
    private final Class<T> type;
    private final ExcelObject excelObject;
    public ExcelReader(Class<T> type) {
        this.type = type;
        this.excelObject = new ExcelObject(type);
    }

    public List<T> readFromExcel() {
        try {
            Workbook workbook = new XSSFWorkbook(loadExcelFile());
            Sheet sheet = workbook.getSheetAt(type.getDeclaredAnnotation(ExcelSheet.class).sheet());
            List<T> members = new ArrayList<>();
            readValues(sheet, members);
            return members;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler o arquivo XLSX", e);
        }
    }

    private void readValues(Sheet sheet, List<T> members) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            members.add(buildMember(row));
        }
    }

    private Map<String, Cell> mapCells(Row row) {
        Map<String, Cell> cells = new HashMap<>();
        Set<String> fields = excelObject.getFieldNames();
        for (String fieldName : fields) {
            Cell cell = row.getCell(excelObject.getCellNumber(fieldName));
            cells.put(fieldName, cell);
        }
        return cells;
    }

    public Cell getCell(String fieldName, Map<String, Cell> cells) {
        return cells.get(fieldName);
    }

    private T buildMember(Row row) {
        return toDomain(mapCells(row));
    }

    private InputStream loadExcelFile() {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("input/users.xlsx");
        if (inputStream == null) {
            throw new RuntimeException("Arquivo não encontrado na pasta input");
        }
        return inputStream;
    }

    public abstract T toDomain(Map<String, Cell> cells);
}
