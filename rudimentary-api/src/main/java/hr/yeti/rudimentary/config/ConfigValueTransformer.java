package hr.yeti.rudimentary.config;

/**
 * <pre>
 * Configuration value transformer function.
 * Transforms input value in any way you like.
 *
 * Used in method {@link ConfigProperty#transform(hr.yeti.rudimentary.config.ConfigValueTransformer) }.
 *
 * @author vedransmid@yeti-it.hr
 * </pre>
 */
@FunctionalInterface
public interface ConfigValueTransformer {

    public Object transform(String value);

}
