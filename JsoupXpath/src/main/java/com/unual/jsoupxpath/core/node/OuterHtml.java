package com.unual.jsoupxpath.core.node;

import com.unual.jsoupxpath.core.NodeTest;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import org.jsoup.nodes.Element;
import java.util.LinkedList;
import java.util.List;

public class OuterHtml implements NodeTest {
    @Override
    public String name() {
        return "outerHtml";
    }

    @Override
    public XValue call(Scope scope) {
        List<String> res = new LinkedList<>();
        for (Element e:scope.context()){
            res.add(e.outerHtml());
        }
        return XValue.create(res);
    }
}
