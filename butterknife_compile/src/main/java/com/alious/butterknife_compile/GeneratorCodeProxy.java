package com.alious.butterknife_compile;

import java.util.List;

import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class GeneratorCodeProxy {

    private String packageName;
    private String classSimpleName;
    private Elements mElements;
    private List<VariableElement> mVariableElementList;

    public GeneratorCodeProxy(Elements elements, String packageName, String classSimpleName) {
        this.mElements = elements;
        this.packageName = packageName;
        this.classSimpleName = classSimpleName;
    }

    public void addElement(VariableElement element) {
        mVariableElementList.add(element);
    }

    public void generateCode() {



    }

}
