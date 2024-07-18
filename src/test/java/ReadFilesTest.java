import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Meals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadFilesTest {

    private final ClassLoader classLoader = ReadFilesTest.class.getClassLoader();

    @DisplayName("Проверка полей в файле формата json")
    @Test
    void jsonParsingTest() throws IOException {
        try (InputStream is = classLoader.getResourceAsStream("jsonTest.json")) {
            ObjectMapper mapper = new ObjectMapper();
            Meals meals = mapper.readValue(is, Meals.class);
            assertThat(meals.getNumber()).isEqualTo(135);
            assertThat(meals.getTitle()).isEqualTo("fried eggs");
            assertThat(meals.getIngredients().getFirstIngredient()).isEqualTo("eggs");
            assertThat(meals.getIngredients().getSecondIngredient()).isEqualTo("bacon");
            assertThat(meals.getIngredients().getThirdIngredient()).isEqualTo("cheese");
        }
    }

    @DisplayName("Проверка полей в заархивированном файле формата csv ")
    @Test
    void csvParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                classLoader.getResourceAsStream("files.zip"))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    CSVReader csv = new CSVReader(new InputStreamReader(zis));
                    List<String[]> data = csv.readAll();
                    Assertions.assertEquals(2, data.size());
                    Assertions.assertArrayEquals(
                            new String[]{"director", "Ivanov"},
                            data.get(0)
                    );
                    Assertions.assertArrayEquals(
                            new String[]{"cleaner", "Petrov"},
                            data.get(1)
                    );
                }
            }
        }
    }

    @DisplayName("Проверка полей в заархивированном файле формата xlsx ")
    @Test
    void xlsxParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                classLoader.getResourceAsStream("files.zip"))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xlsx")) {
                    XLS xls = new XLS(zis);

                    String cellValue = xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();
                    Assertions.assertTrue(cellValue.contains("excel-файл для домашней работы"));
                }
            }
        }
    }

    @DisplayName("Проверка полей в заархивированном файле формата pdf ")
    @Test
    void pdfParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                classLoader.getResourceAsStream("files.zip"))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".pdf")) {
                    PDF pdf = new PDF(zis);
                    Assertions.assertTrue(pdf.text.contains("PDF-файл для домашней работы"));
                }
            }
        }
    }
}
