package kanban.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class CsvStringTest {

    static String[] headerTest;

    @BeforeAll
    static void beforeAllTest() {
        headerTest = new String[]{"id", "type", "name", "status", "description", "epic"};
    }
    
    
    @Test
    void givenCsvHeader_whenHeaderParing_thenParsingCorrected() {
        CsvString csvString = new CsvString("id,type,name,status,description,epic");

        Map<Integer, String> csvHeaderMap = new HashMap<>(csvString.getCsvHeaderMap());
        for (int i = 0; i < headerTest.length; i++) {
            assertEquals(headerTest[i], csvHeaderMap.get(i));
        }
    }

    @Test
    void givenCsvStringWithHeader_whenStringParsed_thenParsedCorrectly() {
        CsvString csvString = new CsvString("id,type,name,status,description,epic");
        Optional<List<String>> csvParsedString = csvString.parseCsv("1,2,3,4,5,6");
        assertTrue(csvParsedString.isPresent());
        assertEquals(csvParsedString.get(), List.of("1", "2", "3", "4", "5", "6"));

        csvParsedString = csvString.parseCsv("1,2,3,4,5,");
        assertTrue(csvParsedString.isPresent());
        assertEquals(csvParsedString.get(), List.of("1", "2", "3", "4", "5", ""));

        csvParsedString = csvString.parseCsv("1,2,3,4,5,6,7");
        assertFalse(csvParsedString.isPresent());
    }

    @Test
    void givenCsvStrings_whenStringParsed_thenParsedCorrectly() {
        Map<String, List<String>> csvParseTest = Map.of(
                "1,2,3", List.of("1", "2", "3"),
                "1,2,3,", List.of("1", "2", "3", ""),
                "\"1, \"\"2,3\"\"\",4,,5", List.of("1, \"2,3\"", "4", "", "5"),
                "1, \"\"", List.of("1", " \""),
                "1,\"\"", List.of("1", ""),
                "\"1,2\",\"3,4\"", List.of("1,2", "3,4"),
                "\"1", List.of(""), // Parsing error
                "1\"", List.of(""), // Parsing error
                "123, \"\"\"\"\"", List.of(""), // Parsing error
                "123,\"\"\"\"\"", List.of("") // Parsing error
        );

        for (String csvSample : csvParseTest.keySet()) {
            CsvString csvString = new CsvString();
            Optional<List<String>> csvParsedString = csvString.parseCsv(csvSample);
            if (csvParsedString.isPresent()) {
                assertEquals(csvParseTest.get(csvSample), csvParsedString.get());
            } else {
                assertEquals(csvParseTest.get(csvSample), List.of(""));
            }
        }
    }

}

