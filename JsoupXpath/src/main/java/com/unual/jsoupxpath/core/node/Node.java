package com.unual.jsoupxpath.core.node;

import com.unual.jsoupxpath.core.NodeTest;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 获取当前节点下所有子节点以及独立文本
 *
 * @author: github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/4/4.
 */
public class Node implements NodeTest {
    /**
     * 支持的函数名
     */
    @Override
    public String name() {
        return "node";
    }

    @Override
    public XValue call(Scope scope) {
        Elements context = new Elements();
        for (Element el : scope.context()) {
            context.addAll(el.children());
            String txt = el.ownText();
            if (StringUtils.isNotBlank(txt)) {
                Element et = new Element("");
                et.appendText(txt);
                context.add(et);
            }
        }
        return XValue.create(context);
    }
}
