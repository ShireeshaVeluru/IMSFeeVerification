package com.ims.automation.utilities;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelUtility {

    private ExcelUtility() {
        // Prevent object creation
    }

    /**
     * Returns complete sheet data excluding header row.
     *
     * @param filePath  Excel file path
     * @param sheetName Sheet name
     * @return Object[][]
     */
    public static Object[][] getSheetData(String filePath, String sheetName) {

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet not found : " + sheetName);
            }

            int totalRows = sheet.getLastRowNum();
            int totalColumns = sheet.getRow(0).getLastCellNum();

            Object[][] data = new Object[totalRows][totalColumns];

            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= totalRows; i++) {

                Row row = sheet.getRow(i);

                for (int j = 0; j < totalColumns; j++) {

                    if (row == null || row.getCell(j) == null) {
                        data[i - 1][j] = "";
                    } else {
                        data[i - 1][j] =
                                formatter.formatCellValue(row.getCell(j));
                    }

                }

            }

            return data;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read Excel file : " + filePath, e);

        }
    }

    /**
     * Returns a single cell value.
     *
     * @param filePath Excel file path
     * @param sheetName Sheet name
     * @param rowNumber Row number
     * @param columnNumber Column number
     * @return Cell value as String
     */
    public static String getCellData(String filePath,
                                     String sheetName,
                                     int rowNumber,
                                     int columnNumber) {

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet not found : " + sheetName);
            }

            Row row = sheet.getRow(rowNumber);

            if (row == null || row.getCell(columnNumber) == null) {
                return "";
            }

            DataFormatter formatter = new DataFormatter();

            return formatter.formatCellValue(row.getCell(columnNumber));

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read Excel file : " + filePath, e);

        }
    }

}