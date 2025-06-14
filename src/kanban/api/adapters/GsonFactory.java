package kanban.api.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Factory class for creating a customized Gson instance.
 * This class provides a Gson configuration that supports serialization and deserialization
 * for {@link LocalDateTime} and {@link Duration} types using ISO-8601 formatting.
 */
public class GsonFactory {

    /**
     * A shared formatter for LocalDateTime using ISO_LOCAL_DATE_TIME pattern.
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Creates and returns a customized Gson instance with support for
     * LocalDateTime and Duration serialization and deserialization.
     * LocalDateTime is serialized to and deserialized from ISO-8601 string format.
     * Duration is serialized to and deserialized from its ISO-8601 string representation.
     *
     * @return a Gson instance configured for LocalDateTime and Duration types
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>)
                                (src, typeOfSrc, context) ->
                                        new JsonPrimitive(src.format(formatter)))
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>)
                                (json, typeOfT, context) ->
                                        LocalDateTime.parse(json.getAsString(), formatter))
                .registerTypeAdapter(
                        Duration.class,
                        (JsonSerializer<Duration>)
                                (src, typeOfSrc, context) ->
                                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(
                        Duration.class,
                        (JsonDeserializer<Duration>)
                                (json, typeOfT, context) ->
                                        Duration.parse(json.getAsString()))
                .create();
    }
}
