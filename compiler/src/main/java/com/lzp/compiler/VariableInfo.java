package com.lzp.compiler;

import javax.lang.model.element.VariableElement;

/**
 * Created by lzp48947 on 2018/5/30.
 */

public class VariableInfo {
    private int viewId;
    private VariableElement variableElement;

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public VariableElement getVariableElement() {
        return variableElement;
    }

    public void setVariableElement(VariableElement variableElement) {
        this.variableElement = variableElement;
    }
}
