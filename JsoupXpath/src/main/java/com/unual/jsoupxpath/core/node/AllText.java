package com.unual.jsoupxpath.core.node;

import com.unual.jsoupxpath.core.NodeTest;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import org.jsoup.nodes.Element;

import java.util.LinkedList;
import java.util.List;

public class AllText implements NodeTest {
    /**
     * 支持的函数名
     */
    @Override
    public String name() {
        return "allText";
    }

    @Override
    public XValue call(Scope scope) {
        List<String> res = new LinkedList<>();
        for (Element e : scope.context()) {
            if ("script".equals(e.nodeName())) {
                res.add(e.data());
            } else {
                res.add(e.text());
            }
        }
        return XValue.create(res);
    }
}
