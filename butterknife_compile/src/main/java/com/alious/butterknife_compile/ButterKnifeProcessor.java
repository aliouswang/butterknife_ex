package com.alious.butterknife_compile;

import com.alious.butterknife_annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javah.Gen;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    public Messager mMessager;
    public Elements mElements;
    public Filer mFiler;

    private HashMap<String, GeneratorCodeProxy> mProxyHashMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mMessager = processingEnv.getMessager();
        mElements = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();

        mProxyHashMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> bindViewElements = roundEnv.getElementsAnnotatedWith(BindView.class);

        for (Element element : bindViewElements) {
            VariableElement variableElement = (VariableElement) element;
            mMessager.printMessage(Diagnostic.Kind.NOTE, variableElement.getSimpleName().toString());
            Annotation annotation = variableElement.getAnnotation(BindView.class);
            BindView bindView = ((BindView) annotation);
            int value = bindView.value();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "value=" + value);

            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            mMessager.printMessage(Diagnostic.Kind.NOTE, classElement.getQualifiedName().toString());

            String packageName = mElements.getPackageOf(classElement).getQualifiedName().toString();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "package name:" + packageName);

            GeneratorCodeProxy proxy = mProxyHashMap.get(packageName);
            if (proxy == null) {
                proxy = new GeneratorCodeProxy(mElements, packageName, classElement.getQualifiedName().toString());
                mProxyHashMap.put(packageName, proxy);
            }

            proxy.addElement(variableElement);
        }

//        MethodSpec bindMethod = MethodSpec.methodBuilder("main")
//                .addModifiers(Modifier.STATIC)
//                .build();
//
//        TypeSpec helloWorld = TypeSpec.classBuilder("DemoViewBinding")
//                .addMethod(bindMethod)
//                .build();
//
//        JavaFile javaFile = JavaFile.builder("com.alious.helloworld", helloWorld)
//                .build();
//
//        try {
//            javaFile.writeTo(processingEnv.getFiler());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        for (Map.Entry<String, GeneratorCodeProxy> entry : mProxyHashMap.entrySet()) {
            entry.getValue().generateCode();
        }

        mMessager.printMessage(Diagnostic.Kind.WARNING, "process finished!");

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new HashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }
}
