package maow.annotationutils.processing;

public interface RuntimeProcessor {
    void process(Object object);
    void process(Class<?> clazz);
}
