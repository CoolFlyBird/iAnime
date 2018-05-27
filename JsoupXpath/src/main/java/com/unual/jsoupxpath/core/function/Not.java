package com.unual.jsoupxpath.core.function;


import com.unual.jsoupxpath.core.Function;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;
import com.unual.jsoupxpath.exception.XpathParserException;

import java.util.List;

/**
 * bool not(bool)
 *
 * @author github.com/hermitmmll
 * @since 2018/4/3.
 */
public class Not implements Function {
    @Override
    public String name() {
        return "not";
    }

    @Override
    public XValue call(Scope scope, List<XValue> params) {
        if (params.size() == 1 && params.get(0).isBoolean()) {
            return XValue.create(!params.get(0).asBoolean());
        } else {
            throw new XpathParserException("error param in not(bool) function.Please check.");
        }
    }
}
