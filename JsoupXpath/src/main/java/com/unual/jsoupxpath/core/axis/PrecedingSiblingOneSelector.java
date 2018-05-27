package com.unual.jsoupxpath.core.axis;

import com.unual.jsoupxpath.core.AxisSelector;
import com.unual.jsoupxpath.core.XValue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class PrecedingSiblingOneSelector implements AxisSelector {
    /**
     * assign name
     *
     * @return name
     */
    @Override
    public String name() {
        return "preceding-sibling-one";
    }

    /**
     * @param context
     * @return res
     */
    @Override
    public XValue apply(Elements context) {
        Set<Element> total = new HashSet<>();
        for (Element el : context) {
            if (el.previousElementSibling() != null) {
                total.add(el);
            }
        }
        Elements newContext = new Elements();
        newContext.addAll(total);
        return XValue.create(newContext);
    }
}
