package hr.yeti.rudimentary.http;

import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.content.View;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import java.lang.reflect.ParameterizedType;

public class HttpEndpointUtils {

    public static Class<? extends Model> getRequestBodyType(Class<?> clazz) throws ClassNotFoundException {
        try {
            String className = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
            return (Class<? extends Model>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class is not parametrized with generic type.", e);
        }
    }

    public static Class<? extends Model> getResponseBodyType(Class<?> clazz) throws ClassNotFoundException {
        try {
            if (isViewEndpoint(clazz)) {
                return View.class;
            }
            String className = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[1].getTypeName();
            return (Class<? extends Model>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class is not parametrized with generic type.", e);
        }
    }

    public static Class<? extends Model> getGenericType(Class<?> clazz, int index) throws ClassNotFoundException {
        try {
            String className = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[index].getTypeName();
            return (Class<? extends Model>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class is not parametrized with generic type.", e);
        }
    }

    public static boolean isViewEndpoint(Class<?> clazz) {
        return clazz.getGenericInterfaces()[0].getTypeName().startsWith(ViewEndpoint.class.getCanonicalName());
    }
}
