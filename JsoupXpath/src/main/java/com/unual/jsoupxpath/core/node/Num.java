package com.unual.jsoupxpath.core.node;

import com.unual.jsoupxpath.core.NodeTest;
import com.unual.jsoupxpath.core.Scope;
import com.unual.jsoupxpath.core.XValue;
import com.unual.jsoupxpath.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Num implements NodeTest {
    private static Pattern numExt = Pattern.compile("\\d*\\.?\\d+");

    /**
     * 支持的函数名
     */
    @Override
    public String name() {
        return "num";
    }

    @Override
    public XValue call(Scope scope) {
        NodeTest textFun = Scanner.findNodeTestByName("allText");
        XValue textVal = textFun.call(scope);
        String whole = StringUtils.join(textVal.asList(), "");
        Matcher matcher = numExt.matcher(whole);
        if (matcher.find()) {
            String numStr = matcher.group();
            BigDecimal num = new BigDecimal(numStr);
            return XValue.create(num.doubleValue());
        } else {
            return XValue.create(null);
        }
    }
}
