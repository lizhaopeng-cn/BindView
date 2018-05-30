package com.lzp.inject;

import android.app.Activity;

/**
 * Created by lzp48947 on 2018/5/30.
 */

public class ButterKnife {
    public static void bind(Activity activity){
        bind(activity, activity);
    }

    public static void bind(Object host, Object root){
        Class<?> c = host.getClass();
        String className = c.getName() + "$$ViewInjector";
        try {
            Class<?> injectC = Class.forName(className);
            ViewInject viewInject = (ViewInject) injectC.newInstance();
            viewInject.inject(host,root);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
