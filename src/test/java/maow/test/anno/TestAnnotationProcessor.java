package maow.test.anno;

import maow.annotationutils.processing.AbstractRuntimeProcessor;

public class TestAnnotationProcessor extends AbstractRuntimeProcessor<TestAnnotation> {
    public TestAnnotationProcessor() {
        super(TestAnnotation.class);
    }

    @Override
    protected void process(TestAnnotation annotation, Class<?> clazz, Object object) {
        System.out.println(annotation.value());
    }
}