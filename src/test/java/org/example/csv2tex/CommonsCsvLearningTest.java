package org.example.csv2tex;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Just to try out how commons CSV works
 */
public class CommonsCsvLearningTest {

    @Test
    public void tryOutCommonsCsvParsing() throws IOException {
        File csvFile = new File(getClass().getClassLoader().getResource("testCsv.csv").getFile());
        Reader in = new FileReader(csvFile);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader("name", "email", "comment").parse(in);

        List<CSVRecord> recordList = new ArrayList<>();
        records.forEach(recordList::add);
        CSVRecord record1 = recordList.get(1);
        CSVRecord record2 = recordList.get(2);
        assertThat(record1.get(0)).isEqualTo("robert");
        assertThat(record2.get(0)).isEqualTo("micha");
    }
}
