package vn.edu.fpt.transitlink.shared.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.lang.reflect.Field;

public class ExcelFileUtils {

    /**
     * Đọc Excel và trả về List<Map<String,Object>>
     */
    public static List<Map<String, Object>> readExcelAsMap(String filePath) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Workbook workbook = createWorkbook(filePath)) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Not found header row at index 0");
            }

            List<String> headers = extractHeaders(headerRow);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                Map<String, Object> rowMap = buildRowMap(row, headers);
                results.add(rowMap);
            }
        }
        return results;
    }

    /**
     * Đọc Excel và map sang List<POJO>
     */
    public static <T> List<T> readExcelAsPojo(String filePath, Class<T> targetType) throws Exception {
        List<T> results = new ArrayList<>();

        try (Workbook workbook = createWorkbook(filePath)) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Not found header row at index 0");
            }

            List<String> headers = extractHeaders(headerRow);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                T obj = buildPojo(row, headers, targetType);
                results.add(obj);
            }
        }
        return results;
    }

    // ===== Helper methods =====

    private static Workbook createWorkbook(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        if (filePath.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (filePath.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            fis.close();
            throw new IllegalArgumentException("Only .xls or .xlsx files are supported");
        }
    }

    private static List<String> extractHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(getCellValueAsString(cell));
        }
        return headers;
    }

    private static Map<String, Object> buildRowMap(Row row, List<String> headers) {
        Map<String, Object> rowData = new LinkedHashMap<>();
        for (int j = 0; j < headers.size(); j++) {
            Cell cell = row.getCell(j);
            rowData.put(headers.get(j), getCellValue(cell));
        }
        return rowData;
    }

    private static <T> T buildPojo(Row row, List<String> headers, Class<T> clazz) throws Exception {
        Map<String, Object> rowMap = buildRowMap(row, headers);
        T obj = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = rowMap.get(field.getName());
            if (value == null) {
                value = findValueIgnoreCase(rowMap, field.getName());
            }
            if (value != null) {
                field.set(obj, convertValue(value, field.getType()));
            }
        }
        return obj;
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                }
                double d = cell.getNumericCellValue();
                return (d == Math.floor(d)) ? (int) d : d;
            case BOOLEAN: return cell.getBooleanCellValue();
            case FORMULA: return cell.getCellFormula();
            default:      return null;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        Object value = getCellValue(cell);
        return value != null ? value.toString() : "";
    }

    private static boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                if (!getCellValueAsString(cell).trim().isEmpty()) return false;
            }
        }
        return true;
    }

    private static Object findValueIgnoreCase(Map<String, Object> data, String fieldName) {
        return data.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(fieldName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) return value;

        String s = value.toString();
        if (targetType == String.class) return s;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(s);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(s);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(s);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(s);
        if (targetType == LocalDateTime.class && value instanceof LocalDateTime) return value;

        return value;
    }
}
