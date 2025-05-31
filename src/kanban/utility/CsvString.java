package kanban.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for handling CSV header parsing and formatting.
 * This class provides methods to parse a CSV header string, format strings for CSV inclusion,
 * and parse CSV lines into individual entries.
 */
public class CsvString {

    private final String csvHeader;
    private final Map<Integer, String> csvHeaderMap;

    /**
     * Default constructor.
     * Initializes an empty CSV header and an empty header map.
     */
    public CsvString() {
        this.csvHeader = null;
        this.csvHeaderMap = null;
    }

    /**
     * Constructor that accepts a CSV header string.
     * Initializes the CSV header and parses the header into a map.
     *
     * @param csvHeader the CSV header string to parse
     */
    public CsvString(String csvHeader) {
        this.csvHeader = csvHeader;
        this.csvHeaderMap = parseCsvHeader(csvHeader);
    }

    /**
     * Gets the CSV header string.
     *
     * @return the CSV header string
     */
    public String getCsvHeader() {
        return csvHeader;
    }

    /**
     * Gets the parsed CSV header map where the key is the column index and the
     * value is the column name.
     *
     * @return the parsed CSV header map
     */
    public Map<Integer, String> getCsvHeaderMap() {
        return csvHeaderMap;
    }

    /**
     * Parses the CSV header string into a map where the key is the column index
     * and the value is the column name.
     *
     * @param csvHeader the CSV header string to parse
     * @return a map with the column index as the key and column name as the value
     */
    private Map<Integer, String> parseCsvHeader(String csvHeader) {

        String[] preparsedHeader = csvHeader.split(",");

        int position = 0;
        Map<Integer, String> parsedHeader = new HashMap<>();

        for (String entry : preparsedHeader) {
            parsedHeader.put(position++, entry);
        }

        return parsedHeader;
    }

    /**
     * Formats a string for safe inclusion in a CSV file.
     * If the input string contains special characters such as a comma ({@code ,}),
     * a double quote ({@code "}"), a newline ({@code \n}), or a carriage return ({@code \r}),
     * the string is enclosed in double quotes, and all existing double quotes are escaped
     * by doubling them (i.e., {@code "} becomes {@code ""}).
     * If no special characters are found, the string is returned unchanged
     * (except for escaping quotes).
     *
     * @param composeString the original string to format for CSV output
     * @return the formatted string safe for CSV inclusion
     */
    public String toCsvEntry(String composeString) {
        boolean isNeedCompose = composeString.contains(",")
                || composeString.contains("\"") || composeString.contains("\n")
                || composeString.contains("\r");

        String compose = composeString.replace("\"", "\"\"");

        return isNeedCompose ? "\"" + compose + "\"" : compose;
    }

    /**
     * Parses a CSV-formatted string into a list of values.
     * This method handles values enclosed in double quotes and correctly processes escaped quotes
     * (i.e., {@code ""} becomes {@code "}). It also ensures that commas within quoted strings
     * are not treated as delimiters. If the last character of the input is a comma,
     * a space is appended to handle potential empty trailing fields.
     * The method validates that the number of parsed fields matches the number of columns
     * in the provided CSV header.
     * If the input is blank, or if parsing fails (e.g., due to unbalanced
     * quotes or column mismatch), an empty {@link Optional} is returned.
     *
     * @param parseCsvString the CSV line to parse
     * @return an {@link Optional} containing the list of parsed strings if successful;
     *         otherwise, {@link Optional#empty()}
     */
    public Optional<List<String>> parseCsv(String parseCsvString) {

        if (parseCsvString.isBlank()) {
            return Optional.empty();
        }

        List<String> parsedCsvString = new ArrayList<>();
        StringBuilder parseString = new StringBuilder(parseCsvString);
        boolean isBetweenQuotes = false;
        int currentPosition = 0;
        int startPosition = 0;

        if (parseString.charAt(parseString.length() - 1) == ',') {
            parseString.append(" ");
        } else {
            parseString.append(",");
        }

        do {
            if (currentPosition == parseString.length()) {
                break;
            }
            switch (parseString.charAt(currentPosition)) {
                case '"' -> {
                    if (parseString.charAt(currentPosition + 1) != '"') {
                        isBetweenQuotes = !isBetweenQuotes;
                    } else {
                        currentPosition += 1;
                    }
                    currentPosition += 1;
                }
                case ',' -> {
                    if (!isBetweenQuotes) {
                        // Make postprocessing
                        String postProcessEntry = parseString
                                .substring(startPosition, currentPosition);

                        if (postProcessEntry.startsWith("\"")
                                && postProcessEntry.endsWith("\"")
                                && postProcessEntry.length() >= 2) {

                            postProcessEntry = postProcessEntry
                                    .substring(1, postProcessEntry.length() - 1);
                        }
                        parsedCsvString.add(postProcessEntry
                                .replace("\"\"", "\""));

                        // Continue parsing
                        currentPosition = startPosition = currentPosition + 1;
                    } else {
                        currentPosition += 1;
                    }
                }
                default -> {
                    if (currentPosition == parseString.length() - 1
                            && parseString.charAt(currentPosition) == ' ') {
                        parsedCsvString.add("");
                    }
                    currentPosition += 1;
                }
            }
        } while (parseString.length() != startPosition);

        if (!isBetweenQuotes) {
            if (csvHeaderMap == null) {
                return Optional.of(parsedCsvString);
            } else if (parsedCsvString.size() == csvHeaderMap.size()) {
                return Optional.of(parsedCsvString);
            }
        }
        return Optional.empty();
    }

    /**
     * Splits a CSV-formatted string into a list of individual CSV lines,
     * preserving quoted values that may span multiple lines.
     * This method automatically detects the line separator used in the input
     * (supports {@code \n}, {@code \r\n}, and {@code \r}), and then parses the CSV
     * content, ensuring that lines enclosed in quotes and possibly spanning multiple
     * rows are kept together.
     * Quotes are assumed to be standard CSV-style: double quotes {@code "} are used
     * to enclose fields that may contain newlines or other quotes. Escaped quotes
     * are represented by two double quotes {@code ""}.
     *
     * @param csvString the input CSV string to split, possibly containing quoted lines
     *                  with embedded newlines
     * @return an {@link Optional} containing a list of parsed CSV lines, or
     *         {@link Optional#empty()} if the input is blank or no valid lines are found
     */
    public Optional<List<String>> csvStringSplit(String csvString) {

        // Splitting input string
        String[] splitCsvString = csvString.split("\\R");
        if (splitCsvString.length == 1 && splitCsvString[0].isBlank()) {
            return Optional.empty();
        }

        // Init split process
        List<String> completedStrings = new ArrayList<>();
        StringBuilder processString = new StringBuilder();
        int globalElementsCounter = 0;
        int currentPosition = 0;
        boolean isBetweenQuotes = false;

        while (globalElementsCounter != splitCsvString.length) {

            if (isBetweenQuotes) {
                processString.append(splitCsvString[globalElementsCounter]);
            } else {
                if (!processString.isEmpty()) {
                    completedStrings.add(processString.toString());
                }
                processString = new StringBuilder(splitCsvString[globalElementsCounter]);
                currentPosition = 0;
            }

            globalElementsCounter += 1;

            do {
                if (processString.charAt(currentPosition) == '"') {
                    if (currentPosition + 1 == processString.length()
                            || processString.charAt(currentPosition + 1) != '"') {
                        isBetweenQuotes = !isBetweenQuotes;
                    } else {
                        currentPosition += 1;
                    }
                }
                currentPosition += 1;
            } while (processString.length() != currentPosition);
        }

        if (!isBetweenQuotes && !processString.isEmpty()) {
            completedStrings.add(processString.toString());
        }

        return completedStrings.isEmpty() ? Optional.empty() : Optional.of(completedStrings);
    }
}
