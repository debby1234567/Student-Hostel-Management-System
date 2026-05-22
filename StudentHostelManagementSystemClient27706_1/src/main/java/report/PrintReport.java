/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package report;

import java.awt.HeadlessException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JTable;

/**
 *
 * @author CTRL-SHIFT Ltd
 */
public class PrintReport {
    public void printTable(JTable table) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(table.getPrintable(JTable.PrintMode.FIT_WIDTH, null, null));
            if (job.printDialog()) job.print();
        } catch (HeadlessException | PrinterException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
