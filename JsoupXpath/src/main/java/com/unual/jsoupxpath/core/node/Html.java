package com.unual.jsoupxpath.core.node;

import com.unual.jsoupxpath.core.NodeTest;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;

import org.jsoup.nodes.Element;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取全部节点的内部的html
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/4/9.
 */
public class Html implements NodeTest {
    @Override
    public String name() {
        return "html";
    }

    @Override
    public XValue call(Scope scope) {
        List<String> res = new LinkedList<>();
        for (Element e:scope.context()){
            res.add(e.html());
        }
        return XValue.create(res);
    }
}
