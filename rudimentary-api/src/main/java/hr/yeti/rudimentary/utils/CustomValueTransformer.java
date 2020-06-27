package hr.yeti.rudimentary.utils;

/**
 * <pre>
 * Custom value transformer function.
 * Transforms input value in any way you like.
 *
 * Used in method {@link TRansformable#transform(hr.yeti.rudimentary.config.ConfigValueTransformer) }.
 *
 * @author vedransmid@yeti-it.hr
 * </pre>
 */
@FunctionalInterface
public interface CustomValueTransformer {

    public Object transform(String value);

}
