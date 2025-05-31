package kanban.utililty;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kanban.utility.CsvString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CsvString}, covering header parsing, row parsing,
 * special cases like quoted fields, malformed strings, and multi-line CSV content.
 */
public class CsvStringTest {

    static String[] headerTest;

    @BeforeAll
    static void setUpOnce() {
        headerTest = new String[]{"id", "type", "name", "status", "description", "epic"};
    }

    /**
     * Verifies that a valid CSV header line is parsed into the correct header map.
     */
    @Test
    void shouldParseCsvHeaderCorrectly() {
        CsvString csvString = new CsvString("id,type,name,status,description,epic");
        Assertions.assertNotNull(csvString.getCsvHeaderMap());
        Map<Integer, String> csvHeaderMap = new HashMap<>(csvString.getCsvHeaderMap());
        for (int i = 0; i < headerTest.length; i++) {
            assertEquals(headerTest[i], csvHeaderMap.get(i));
        }
    }

    /**
     * Verifies parsing of valid and invalid CSV data rows against a header.
     */
    @Test
    void shouldParseCsvLineAccordingToHeaderCorrectly() {
        CsvString csvString = new CsvString("id,type,name,status,description,epic");

        Optional<List<String>> parsed = csvString.parseCsv("1,2,3,4,5,6");
        assertTrue(parsed.isPresent());
        assertEquals(List.of("1", "2", "3", "4", "5", "6"), parsed.get());

        parsed = csvString.parseCsv("1,2,3,4,5,");
        assertTrue(parsed.isPresent());
        assertEquals(List.of("1", "2", "3", "4", "5", ""), parsed.get());

        parsed = csvString.parseCsv("1,2,3,4,5,6,7");
        assertFalse(parsed.isPresent());  // Too many fields
    }

    /**
     * Verifies correct parsing behavior of various valid and malformed CSV strings.
     */
    @Test
    void shouldHandleVariousCsvStructuresCorrectly() {
        Map<String, List<String>> csvSamples = Map.of(
                "1,2,3", List.of("1", "2", "3"),
                "1,2,3,", List.of("1", "2", "3", ""),
                "\"1, \"\"2,3\"\"\",4,,5", List.of("1, \"2,3\"", "4", "", "5"),
                "1, \"\"", List.of("1", " \""),
                "1,\"\"", List.of("1", ""),
                "\"1,2\",\"3,4\"", List.of("1,2", "3,4"),
                "\"1", List.of(""),           // Malformed
                "1\"", List.of(""),           // Malformed
                "123, \"\"\"\"\"", List.of(""),  // Malformed
                "123,\"\"\"\"\"", List.of("")   // Malformed
        );

        for (Map.Entry<String, List<String>> entry : csvSamples.entrySet()) {
            CsvString csvString = new CsvString();
            Optional<List<String>> parsed = csvString.parseCsv(entry.getKey());
            if (parsed.isPresent()) {
                assertEquals(entry.getValue(), parsed.get());
            } else {
                assertEquals(entry.getValue(), List.of(""));
            }
        }
    }

    /**
     * Verifies that multi-line CSV content with quoted line breaks is split correctly into records.
     */
    @Test
    void shouldSplitMultiLineCsvContentIntoRecordsCorrectly() {
        String input = """
                Name,Comment
                "Alice","Hello, Bob
                ""Yes"", I agree"
                Bob,"Plain comment"
                ,Blank name
                "Charlie",""";

        List<String> expectedRecords = List.of(
                "Name,Comment",
                "\"Alice\",\"Hello, Bob\"\"Yes\"\", I agree\"",
                "Bob,\"Plain comment\"",
                ",Blank name",
                "\"Charlie\","
        );

        CsvString csvString = new CsvString();
        Optional<List<String>> result = csvString.csvStringSplit(input);

        assertTrue(result.isPresent());
        assertEquals(expectedRecords, result.get());
    }
}
