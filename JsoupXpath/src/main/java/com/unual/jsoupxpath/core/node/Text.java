package com.unual.jsoupxpath.core.node;

import com.unual.jsoupxpath.core.NodeTest;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;
import com.unual.jsoupxpath.util.Scanner;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/2/28.
 */
public class Text implements NodeTest {
    /**
     * 支持的函数名
     */
    @Override
    public String name() {
        return "text";
    }

    @Override
    public XValue call(Scope scope) {
        Elements context = scope.context();
        List<String> res = new LinkedList<>();
        if (context != null && context.size() > 0) {
            if (scope.isRecursion()) {
                NodeTest allTextFun = Scanner.findNodeTestByName("allText");
                return allTextFun.call(scope);
            } else {
                for (Element e : context) {
                    if ("script".equals(e.nodeName())) {
                        res.add(e.data());
                    } else {
                        res.add(e.ownText());
                    }
                }
            }
        }
        return XValue.create(res);
    }
}
