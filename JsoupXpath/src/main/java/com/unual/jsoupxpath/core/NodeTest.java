package com.unual.jsoupxpath.core;


/**
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/2/28.
 */
public interface NodeTest {
    /**
     * 支持的函数名
     */
    String name();

    XValue call(Scope scope);
}
