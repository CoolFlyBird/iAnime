package com.unual.jsoupxpath.core.function;

import com.unual.jsoupxpath.core.Function;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import java.util.List;

/**
 * Function: number count(node-set)
 * The count function returns the number of nodes in the argument node-set.
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/27.
 */
public class Count implements Function {
    @Override
    public String name() {
        return "count";
    }

    @Override
    public XValue call(Scope scope, List<XValue> params) {
        if (params == null || params.size() == 0) {
            return XValue.create(0);
        }
        return XValue.create(params.get(0).asElements().size());
    }
}
