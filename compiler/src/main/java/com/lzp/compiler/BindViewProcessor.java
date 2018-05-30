package com.lzp.compiler;

import com.google.auto.service.AutoService;
import com.lzp.annotation.BindView;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

@AutoService(Process.class)
public class BindViewProcessor extends AbstractProcessor{

    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;
    Map<String, List<VariableInfo>> map = new HashMap<>();
    Map<String, TypeElement> mapClass = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes(){
        Set<String> annotationTypes = new LinkedHashSet<String>();
        annotationTypes.add(BindView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        getInfo(roundEnvironment);

        writeToFile();

        return true;
    }

    private void getInfo(RoundEnvironment roundEnvironment){
        TypeElement typeElement;
        VariableElement variableElement;
        List<VariableInfo> variableInfos = null;

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for(Element element : elements){
            if(element.getKind() == ElementKind.FIELD){
                variableElement = (VariableElement) element;
                typeElement = (TypeElement) variableElement.getEnclosingElement();
                String qualifiedName = typeElement.getQualifiedName().toString();
                variableInfos = map.get(qualifiedName);
                if(variableInfos == null){
                    variableInfos = new ArrayList<>();
                    map.put(qualifiedName, variableInfos);

                    mapClass.put(qualifiedName, typeElement);
                }
                BindView annotation = variableElement.getAnnotation(BindView.class);
                int id = annotation.value();

                VariableInfo variableInfo = new VariableInfo();
                variableInfo.setViewId(id);
                variableInfo.setVariableElement(variableElement);
                variableInfos.add(variableInfo);
            }
        }
    }

    private void writeToFile() {
        try {
            for (String qualifiedName : map.keySet()) {
                TypeElement typeElement = mapClass.get(qualifiedName);

                MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(TypeName.get(typeElement.asType()), "activity").build());
                List<VariableInfo> variableList = map.get(qualifiedName);
                for (VariableInfo variableInfo : variableList) {
                    VariableElement variableElement = variableInfo.getVariableElement();
                    String variableName = variableElement.getSimpleName().toString();
                    String variableFullName = variableElement.asType().toString();
                    constructor.addStatement("activity.$L=($L)activity.findViewById($L)", variableName, variableFullName, variableInfo.getViewId());
                }

                TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "$$ViewInjector")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(constructor.build())
                        .build();

                String packageFullName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                JavaFile javaFile = JavaFile.builder(packageFullName, typeSpec)
                        .build();
                javaFile.writeTo(mFileUtils);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
