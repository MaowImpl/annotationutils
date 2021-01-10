package maow.annotationutils.util;

import java.lang.annotation.Annotation;

public final class AnnotationUtils {
    public static boolean isAnnotated(Class<?> clazz) {
        final Annotation[] annotations = clazz.getAnnotations();
        return annotations.length > 0;
    }

    public static boolean isAnnotated(Object object) {
        return isAnnotated(object.getClass());
    }
}