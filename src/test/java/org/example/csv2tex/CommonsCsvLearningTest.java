package org.example.csv2tex;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Just to try out how commons CSV works
 */
public class CommonsCsvLearningTest {

    // CSVFormat format = aformat.withHeader(Header.class);
    public enum Header {
        Name, Email, Comment
    }

    @Test
    public void tryOutCommonsCsvParsing() throws IOException {
        URL resource = getClass().getClassLoader().getResource("testCsv.csv");
        assertThat(resource)
                .describedAs("file not found in classpath: testCsv.csv")
                .isNotNull();
        File csvFile = new File(resource.getFile());
        Reader in = new FileReader(csvFile);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

        List<CSVRecord> recordList = new ArrayList<>();
        records.forEach(recordList::add);
        assertThat(recordList)
                .describedAs("should contain header, then 2 lines of content")
                .hasSize(3);
        CSVRecord record1 = recordList.get(1);
        CSVRecord record2 = recordList.get(2);
        assertThat(record1.get(0)).isEqualTo("robert");
        assertThat(record1.get(1).trim()).isEqualTo("robert@example.com");
        assertThat(record1.get(2).trim()).isEqualTo("test data set 1");
        assertThat(record2.get(0)).isEqualTo("micha");
        assertThat(record2.get(1).trim()).isEqualTo("micha@example.com");
        assertThat(record2.get(2).trim()).isEqualTo("test data set 2");
    }

    @Test
    public void tryOutCommonsCsvParsingWithOptions() throws IOException {
        URL resource = getClass().getClassLoader().getResource("testCsv.csv");
        assertThat(resource)
                .describedAs("file not found in classpath: testCsv.csv")
                .isNotNull();
        File csvFile = new File(resource.getFile());
        Reader in = new FileReader(csvFile);
        CSVFormat parser = CSVFormat.DEFAULT.builder()
                .setTrim(true)
                .setSkipHeaderRecord(true)
                .setHeader(Header.class)
                .build();

        Iterable<CSVRecord> records = parser.parse(in);

        List<CSVRecord> recordList = new ArrayList<>();
        records.forEach(recordList::add);
        assertThat(recordList)
                .describedAs("should contain 2 lines of content (no header)")
                .hasSize(2);
        CSVRecord record1 = recordList.get(0);
        CSVRecord record2 = recordList.get(1);
        assertThat(record1.get(Header.Name)).isEqualTo("robert");
        assertThat(record1.get(Header.Email)).isEqualTo("robert@example.com");
        assertThat(record1.get(Header.Comment)).isEqualTo("test data set 1");
        assertThat(record2.get(Header.Name)).isEqualTo("micha");
        assertThat(record2.get(Header.Email)).isEqualTo("micha@example.com");
        assertThat(record2.get(Header.Comment)).isEqualTo("test data set 2");
    }
}
