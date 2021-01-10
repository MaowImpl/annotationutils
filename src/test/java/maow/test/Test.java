package maow.test;

import maow.test.anno.TestAnnotationProcessor;

public class Test {
    public static void main(String[] args) {
        final TestAnnotationProcessor processor = new TestAnnotationProcessor();
        processor.process(Annotated.class);
    }
}
