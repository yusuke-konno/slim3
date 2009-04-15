package org.slim3.gen.processor;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.slim3.gen.annotation.Annotations;
import org.slim3.gen.generator.Generator;
import org.slim3.gen.generator.S3ControllerMetaGenerator;
import org.slim3.gen.option.Options;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes(Annotations.Controller)
@SupportedOptions( { Options.DEBUG, Options.META_SUFFIX,
        Options.DATEPATTERN })
public class S3ControllerProcessor extends AbstractProcessor {

    @Override
    protected Generator<TypeElement> createGenerator() {
        return new S3ControllerMetaGenerator(processingEnv);
    }

}