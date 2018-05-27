package com.unual.jsoupxpath.core.axis;

import com.unual.jsoupxpath.core.AxisSelector;
import com.unual.jsoupxpath.core.XValue;
import com.unual.jsoupxpath.util.CommonUtil;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * the preceding axis contains all nodes in the same document as the context node that are before the context
 * node in document order, excluding any ancestors and excluding attribute nodes and namespace nodes
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/27.
 */
public class PrecedingSelector implements AxisSelector {
    /**
     * assign name
     *
     * @return name
     */
    @Override
    public String name() {
        return "preceding";
    }

    /**
     * @param context
     * @return res
     */
    @Override
    public XValue apply(Elements context) {
        Elements preceding = new Elements();
        Set<Element> total = new HashSet<>();
        for (Element el : context) {
            Elements p = el.parents();
            for (Element pe : p) {
                Elements ps = CommonUtil.precedingSibling(pe);
                if (ps == null) {
                    continue;
                }
                total.addAll(ps);
            }
            Elements ps = CommonUtil.precedingSibling(el);
            if (ps == null) {
                continue;
            }
            total.addAll(ps);
        }
        preceding.addAll(total);
        return XValue.create(preceding);
    }
}
