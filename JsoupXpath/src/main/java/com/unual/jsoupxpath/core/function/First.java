package com.unual.jsoupxpath.core.function;


import com.unual.jsoupxpath.core.Function;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import java.util.List;

/**
 * first in xpath is 1
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/29.
 */
public class First implements Function {
    @Override
    public String name() {
        return "first";
    }

    @Override
    public XValue call(Scope scope, List<XValue> params) {
        return XValue.create(1);
    }
}
