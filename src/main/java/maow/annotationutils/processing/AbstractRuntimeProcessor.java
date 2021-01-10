package maow.annotationutils.processing;

import maow.annotationutils.util.RuntimeProcessorException;
import org.reflections.Reflections;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class AbstractRuntimeProcessor<T extends Annotation> implements RuntimeProcessor {
    private static final Reflections REFLECTIONS = new Reflections();

    private final Class<T> annotationClass;

    public AbstractRuntimeProcessor(Class<T> annotationClass) {
        this.annotationClass = annotationClass;
        try {
            checkRuntimeAccessibility();
        } catch (RuntimeProcessorException e) {
            e.printStackTrace();
        }
    }

    private void checkRuntimeAccessibility() throws RuntimeProcessorException {
        if (annotationClass.isAnnotationPresent(Retention.class)) {
            final Retention retention = annotationClass.getAnnotation(Retention.class);
            final RetentionPolicy policy = retention.value();
            if (policy == RetentionPolicy.RUNTIME) {
                return;
            }
        }
        throw new RuntimeProcessorException("Class extending RuntimeProcessor cannot target annotation that does not have a runtime retention policy");
    }

    private void checkTargetValidity(ElementType type) {
        try {
            if (annotationClass.isAnnotationPresent(Target.class)) {
                final Target target = annotationClass.getAnnotation(Target.class);
                final ElementType[] types = target.value();
                final boolean matches = Arrays.asList(types).contains(type);
                if (matches) {
                    return;
                }
            }
            throw new RuntimeProcessorException("Class extending RuntimeProcessor cannot process " + type + " members, specified annotation does not allow targeting that type.");
        } catch (RuntimeProcessorException e) {
            e.printStackTrace();
        }
    }

    public void processAllClasses() {
        checkTargetValidity(ElementType.TYPE);
        for (Class<?> clazz : REFLECTIONS.getTypesAnnotatedWith(annotationClass)) {
            process(clazz);
        }
    }

    public void processAllFields() {
        checkTargetValidity(ElementType.FIELD);
        for (Field field : REFLECTIONS.getFieldsAnnotatedWith(annotationClass)) {
            process(field);
        }
    }

    public void processAllMethods() {
        checkTargetValidity(ElementType.METHOD);
        for (Method method : REFLECTIONS.getMethodsAnnotatedWith(annotationClass)) {
            process(method);
        }
    }

    public void processAllConstructors() {
        checkTargetValidity(ElementType.CONSTRUCTOR);
        for (Constructor<?> constructor : REFLECTIONS.getConstructorsAnnotatedWith(annotationClass)) {
            process(constructor);
        }
    }

    public void processAllObjects(Iterable<Object> objects) {
        for (Object object : objects) {
            process(object);
        }
    }

    public void processAllObjects(Object... objects) {
        for (Object object : objects) {
            process(object);
        }
    }

    public void process(Object object) {
        try {
            final Class<?> clazz = object.getClass();
            if (clazz.isAnnotationPresent(annotationClass)) {
                final T[] annotations = clazz.getAnnotationsByType(annotationClass);
                for (T annotation : annotations) {
                    process(annotation, clazz, object);
                }
                return;
            }
            throw new RuntimeProcessorException("Target for " + this.getClass().getSimpleName() + "#process(Object) does not have annotation of type " + annotationClass.getSimpleName());
        } catch (RuntimeProcessorException e) {
            e.printStackTrace();
        }
    }

    public void process(Class<?> clazz) {
        try {
            if (clazz.isAnnotationPresent(annotationClass)) {
                final T[] annotations = clazz.getAnnotationsByType(annotationClass);
                for (T annotation : annotations) {
                    process(annotation, clazz, null);
                }
                return;
            }
            throw new RuntimeProcessorException("Target for " + this.getClass().getSimpleName() + "#process(Class) does not have annotation of type " + annotationClass.getSimpleName());
        } catch (RuntimeProcessorException e) {
            e.printStackTrace();
        }
    }

    protected abstract void process(T annotation, Class<?> clazz, Object object);
}
