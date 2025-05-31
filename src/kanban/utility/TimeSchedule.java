package kanban.utility;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A utility class for managing time intervals and checking for scheduling conflicts.
 * Time is divided into 10-minute slots for internal representation.
 */
public class TimeSchedule {

    private static final int MINUTES_IN_SLOT = 10;
    private static final String DATE_FORMATTER_WITHOUT_MINUTES = "yyyyMMddHH";
    private static final String DATE_FORMATTER = "yyyyMMddHHmm";

    private final DateTimeFormatter woMinutesFormatter =
            DateTimeFormatter.ofPattern(DATE_FORMATTER_WITHOUT_MINUTES);
    private static final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(DATE_FORMATTER);

    private final Map<String, Boolean> timeTable;

    /**
     * Constructs an empty time schedule.
     */
    public TimeSchedule() {
        timeTable = new HashMap<>();
    }

    /**
     * Constructs a time schedule with an existing timetable.
     *
     * @param timeTable a map of time interval keys to their occupied state
     */
    public TimeSchedule(Map<String, Boolean> timeTable) {
        this.timeTable = new HashMap<>(timeTable);
    }

    /**
     * Returns a copy of the internal time schedule.
     *
     * @return a new map representing the current time schedule
     */
    public Map<String, Boolean> getTimeSchedule() {
        return new HashMap<>(timeTable);
    }

    /**
     * Checks if the given time interval overlaps with any existing interval.
     *
     * @param timeStamp the start time
     * @param duration  the duration
     * @return true if any overlap occurs, false otherwise
     */
    public boolean isTimeOverlapped(LocalDateTime timeStamp, Duration duration) {
        return createTimeIntervals(timeStamp, duration)
                .map(list -> list
                        .stream()
                        .anyMatch(timeTable::containsKey)
                )
                .orElse(true);
    }

    /**
     * Adds a time interval to the schedule.
     *
     * @param timeStamp the start time
     * @param duration  the duration
     */
    public void addTimeInterval(LocalDateTime timeStamp, Duration duration) {
        createTimeIntervals(timeStamp, duration)
                .ifPresent(list -> list.forEach(key -> timeTable.put(key, true)));
    }

    /**
     * Removes a time interval from the schedule.
     *
     * @param timeStamp the start time
     * @param duration  the duration
     */
    public void removeTimeInterval(LocalDateTime timeStamp, Duration duration) {
        createTimeIntervals(timeStamp, duration)
                .ifPresent(list -> list.forEach(timeTable::remove));
    }

    /**
     * Checks whether the given duration is valid (positive and non-zero).
     *
     * @param duration the duration to check
     * @return true if valid, false otherwise
     */
    public boolean isValidDurationValue(Duration duration) {
        return (duration != null
                && !duration.isZero()
                && !duration.isNegative());
    }

    /**
     * Checks whether the given start time is valid (not null or MIN).
     *
     * @param timeStamp the start time
     * @return true if valid, false otherwise
     */
    public boolean isValidStartTimeValue(LocalDateTime timeStamp) {
        return (timeStamp != null && timeStamp != LocalDateTime.MIN);
    }

    /**
     * Checks whether both start time and duration are valid.
     *
     * @param timeStamp the start time
     * @param duration  the duration
     * @return true if both are valid, false otherwise
     */
    public boolean isValidTimeValue(LocalDateTime timeStamp, Duration duration) {
        return isValidStartTimeValue(timeStamp)
                && isValidDurationValue(duration);
    }

    /**
     * Converts a LocalDateTime to a formatted string.
     *
     * @param timeStamp the date-time value
     * @return formatted string
     */
    public static String composeLocalDateTime(LocalDateTime timeStamp) {
        if (timeStamp == null) {
            timeStamp = LocalDateTime.MIN;
        }
        return timeStamp.format(dateFormatter);
    }

    /**
     * Converts a Duration to a string representing seconds.
     *
     * @param duration the duration
     * @return string of seconds
     */
    public static String composeDuration(Duration duration) {
        if (duration == null) {
            duration = Duration.ZERO;
        }
        return String.valueOf(duration.getSeconds());
    }

    /**
     * Parses a string to LocalDateTime using a standard format.
     *
     * @param dateTimeString the input string
     * @return an optional containing the parsed date-time
     */
    public static Optional<LocalDateTime> parseLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(dateTimeString, dateFormatter));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parses a string representing seconds into a Duration.
     *
     * @param secondsString the string to parse
     * @return an optional containing the duration
     */
    public static Optional<Duration> parseDurationFromSeconds(String secondsString) {
        if (secondsString == null || secondsString.isEmpty()) {
            return Optional.empty();
        }
        try {
            long seconds = Long.parseLong(secondsString);
            return Optional.of(Duration.ofSeconds(seconds));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Splits a time range into discrete 10-minute interval keys.
     *
     * @param timeStamp the start time
     * @param duration  the duration
     * @return an optional list of string interval keys
     */
    private Optional<List<String>> createTimeIntervals(LocalDateTime timeStamp, Duration duration) {
        if (timeStamp == LocalDateTime.MIN
                || duration.compareTo(Duration.ofDays(365)) >= 0
                || duration.compareTo(Duration.ZERO) == 0
                || duration.compareTo(Duration.ZERO) <= 0) {
            return Optional.empty();
        }

        int numberOfIntervals = (int) Math.ceil((double) Math.max((int) duration.toMinutes(),
                MINUTES_IN_SLOT) / MINUTES_IN_SLOT);

        List<String> intervalsList = IntStream.range(0, numberOfIntervals)
                .mapToObj(i -> timeStamp.plusMinutes((long) i * MINUTES_IN_SLOT))
                .map(this::createTimeIntervalString)
                .toList();

        return intervalsList.isEmpty() ? Optional.empty() : Optional.of(intervalsList);
    }

    /**
     * Generates a unique string key for a 10-minute interval.
     *
     * @param timeStamp the timestamp for the interval
     * @return formatted string key for internal timetable
     */
    private String createTimeIntervalString(LocalDateTime timeStamp) {
        String formattedTimeStamp = timeStamp.format(woMinutesFormatter);

        int interval = (int) Math.floor((double) timeStamp.getMinute() / MINUTES_IN_SLOT);
        int startInterval = interval * MINUTES_IN_SLOT;
        int endInterval = startInterval + MINUTES_IN_SLOT == 60 ? 59 :
                startInterval + MINUTES_IN_SLOT;

        return formattedTimeStamp
                + String.format("%02d", startInterval)
                + String.format("%02d", timeStamp.getHour())
                + String.format("%02d", endInterval);
    }
}
