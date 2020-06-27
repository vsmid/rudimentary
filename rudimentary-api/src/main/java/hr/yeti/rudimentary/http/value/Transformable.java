package hr.yeti.rudimentary.http.value;

import hr.yeti.rudimentary.config.ConfigException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Transformable {

    protected String value;

    public Integer asInt() {
        return Integer.valueOf(value);
    }

    /**
     * Gets value as long.
     *
     * @return Value as long.
     */
    public Long asLong() {
        return Long.valueOf(value);
    }

    /**
     * Gets value as double.
     *
     * @return Value as double.
     */
    public Double asDouble() {
        return Double.valueOf(value);
    }

    /**
     * Gets value as boolean.
     *
     * @return Value as boolean.
     */
    public Boolean asBoolean() {
        return Boolean.valueOf(value);
    }

    /**
     * <pre>
     * Gets value as array of strings.
     * It is mandatory that property value is given as comma separated string values.
     *
     * e.g. cities=Zagreb, Podlonk, Zali Log.
     *
     * </pre>
     *
     * @return Value as array of string.
     */
    public String[] asArray() {
        return Stream.of(value.split(","))
            .map(String::trim)
            .filter(val -> !val.isEmpty())
            .toArray(String[]::new);
    }

    /**
     * Gets value as {@link URI}.
     *
     * @return Value as {@link URI}.
     */
    public URI asURI() {
        return URI.create(value);
    }

    /**
     * Gets value as {@link URL}.
     *
     * @return Value as {@link URL}.
     */
    public URL asURL() {
        try {
            return new URL(value);
        } catch (MalformedURLException ex) {
            throw new ConfigException(ex);
        }
    }

    /**
     * Gets value as {@link Map}. Property value must be in form of k1=v1,k2=v2...
     *
     * @return Value as {@link Map}.
     */
    public Map<String, String> asMap() {
        String[] values = value.split(",");

        return Stream.of(values)
            .map(kv -> kv.split("="))
            .collect(
                Collectors.toMap(
                    kv -> kv[0].trim(),
                    kv -> kv[1].trim(),
                    (v1, v2) -> v2,
                    TreeMap::new
                )
            );
    }

    /**
     * Gets value as {@link Path}. Value can be in form of part1/part2/part3 which will be treated as already
     * constructed path or in a form of part1,part2,part3... in which case the path will be constructed by this method.
     *
     * @return Value as {@link Path}.
     */
    public Path asPath() {
        String[] path = value.split(",");

        String[] remainingPath = Stream.of(path)
            .map(String::trim)
            .skip(1)
            .toArray(String[]::new);

        return Path.of(
            path[0].trim(),
            remainingPath
        );
    }

    /**
     * Gets value after transformer function appliance.
     *
     * @param transformer Transformer function.
     * @return Transformed property value.
     */
    public Object transform(CustomValueTransformer transformer) {
        return transformer.transform(value);
    }

    /**
     * Gets property value.
     *
     * @return Configuration property value as string.
     */
    public String value() {
        return this.value;
    }

    /**
     * Checks is value is blank.
     *
     * @param trim Perform {@link String#trim()} on property value before returning result.
     * @return Whether or not value is blank(length = 0) as boolean.
     */
    public boolean isBlank(boolean trim) {
        if (isNull()) {
            return false;
        }
        String val = trim ? value.trim() : value;
        return val.length() == 0;
    }

    /**
     * Checks is value is null.
     *
     * @return Whether or not value is null.
     */
    public boolean isNull() {
        return Objects.isNull(value);
    }

    /**
     * Gets value as string.
     *
     * @return Value as string.
     */
    @Override
    public String toString() {
        return value;
    }

}
