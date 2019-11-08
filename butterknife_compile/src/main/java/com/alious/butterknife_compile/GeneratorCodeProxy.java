package com.alious.butterknife_compile;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class GeneratorCodeProxy {

    private String packageName;
    private String classSimpleName;
    private Elements mElements;
    private Filer mFiler;
    private List<VariableElement> mVariableElementList = new ArrayList<>();

    public GeneratorCodeProxy(Filer filer, Elements elements, String packageName, String classSimpleName) {
        this.mFiler = filer;
        this.mElements = elements;
        this.packageName = packageName;
        this.classSimpleName = classSimpleName;
    }

    public void addElement(VariableElement element) {
        mVariableElementList.add(element);
    }

    public void generateCode() {


        MethodSpec methodSpec = MethodSpec.methodBuilder("bind")
                .build();

        TypeSpec classSpec = TypeSpec.classBuilder(classSimpleName + "_ViewBinding")
                .addMethod(methodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, classSpec)
                .build();

        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
