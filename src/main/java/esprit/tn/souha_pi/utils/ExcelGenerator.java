package esprit.tn.souha_pi.utils;

import esprit.tn.souha_pi.entities.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelGenerator {

    public static void generate(String path, List<Transaction> transactions) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        Row header = sheet.createRow(0);
        String[] columns = {"Date", "Type", "Montant", "Avec", "Description", "Statut"};

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        int rowIndex = 1;

        for (Transaction t : transactions) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(sdf.format(t.getCreatedAt()));
            row.createCell(1).setCellValue(t.getType());
            row.createCell(2).setCellValue(t.getAmount());
            row.createCell(3).setCellValue(t.getTarget());
            row.createCell(4).setCellValue(t.getType());
            row.createCell(5).setCellValue(t.getAmount() > 0 ? "Reçu" : "Envoyé");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fos = new FileOutputStream(path);
        workbook.write(fos);
        workbook.close();
        fos.close();
    }
}