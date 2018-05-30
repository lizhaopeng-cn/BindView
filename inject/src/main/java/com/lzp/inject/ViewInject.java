package com.lzp.inject;

/**
 * Created by lzp48947 on 2018/5/30.
 */

public interface ViewInject<T> {
    void inject(T t, Object o);
}
