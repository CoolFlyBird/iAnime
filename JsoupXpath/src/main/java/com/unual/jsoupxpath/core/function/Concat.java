package com.unual.jsoupxpath.core.function;

import com.unual.jsoupxpath.core.Function;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import java.util.List;

/**
 * Function: string concat(string, string, string*)
 * The concat function returns the concatenation of its arguments.
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class Concat implements Function {
    @Override
    public String name() {
        return "concat";
    }

    @Override
    public XValue call(Scope scope, List<XValue> params) {
        StringBuilder accum = new StringBuilder();
        for (XValue v : params) {
            accum.append(v.asString());
        }
        return XValue.create(accum.toString());
    }
}
