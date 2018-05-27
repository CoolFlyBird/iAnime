package com.unual.jsoupxpath.core.axis;

import com.unual.jsoupxpath.core.AxisSelector;
import com.unual.jsoupxpath.core.XValue;

import org.jsoup.select.Elements;

/**
 * the self axis contains just the context node itself
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/2/28.
 */
public class SelfSelector implements AxisSelector {
    @Override
    public String name() {
        return "self";
    }

    @Override
    public XValue apply(Elements es) {
        return XValue.create(es);
    }
}
