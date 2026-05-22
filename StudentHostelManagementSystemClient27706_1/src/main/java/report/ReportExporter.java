/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package report;

import java.io.FileOutputStream;
import java.util.List;
import model.Student;
import org.apache.poi.xssf.usermodel.*;
/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class ReportExporter {
    public void exportStudentsToExcel(List<Student> students, String path) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Students");
            int rowNum = 0;
            for (Student s : students) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getStudentId());
                row.createCell(1).setCellValue(s.getName());
                row.createCell(2).setCellValue(s.getEmail());
            }
            try (FileOutputStream out = new FileOutputStream(path)) {
                workbook.write(out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
