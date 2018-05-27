package com.unual.jsoupxpath.core.function;

import com.unual.jsoupxpath.core.Function;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class SubStringEx implements Function {
    @Override
    public String name() {
        return "substring-ex";
    }

    @Override
    public XValue call(Scope scope, List<XValue> params) {
        String target = params.get(0).asString();
        int start = params.get(1).asLong().intValue();
        if (params.get(2) != null) {
            int end = params.get(2).asLong().intValue();
            return XValue.create(StringUtils.substring(target, start, end));
        }
        return XValue.create(StringUtils.substring(target, start));
    }
}
