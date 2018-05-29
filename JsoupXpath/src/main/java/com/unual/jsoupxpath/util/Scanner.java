package com.unual.jsoupxpath.util;

import com.unual.jsoupxpath.core.AxisSelector;
import com.unual.jsoupxpath.core.Function;
import com.unual.jsoupxpath.core.NodeTest;
import com.unual.jsoupxpath.core.axis.*;
import com.unual.jsoupxpath.core.function.*;
import com.unual.jsoupxpath.core.node.*;
import com.unual.jsoupxpath.exception.NoSuchAxisException;
import com.unual.jsoupxpath.exception.NoSuchFunctionException;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private static final Class AXIS[] = new Class[]{AncestorOrSelfSelector.class, AncestorSelector.class, AttributeSelector.class, ChildSelector.class, DescendantOrSelfSelector.class, DescendantSelector.class, FollowingSelector.class, FollowingSiblingOneSelector.class, FollowingSiblingSelector.class, ParentSelector.class, PrecedingSelector.class, PrecedingSiblingOneSelector.class, PrecedingSiblingSelector.class, SelfSelector.class};
    private static final Class FUN[] = new Class[]{Concat.class, Contains.class, Count.class, First.class, Last.class, Not.class, Position.class, StartsWith.class, StringLength.class, SubString.class, SubStringAfter.class, SubStringBefore.class, SubStringEx.class};
    private static final Class NODE[] = new Class[]{AllText.class, Html.class, Node.class, Num.class, OuterHtml.class, Text.class};
    private static Map<String, AxisSelector> axisSelectorMap = new HashMap<>();
    private static Map<String, NodeTest> nodeTestMap = new HashMap<>();
    private static Map<String, Function> functionMap = new HashMap<>();

    static {
        for (Class clazz : AXIS) {
            registerAxisSelector(clazz);
        }
        for (Class clazz : FUN) {
            registerFunction(clazz);
        }
        for (Class clazz : NODE) {
            registerNodeTest(clazz);
        }
    }

    public static AxisSelector findSelectorByName(String selectorName) {
        AxisSelector selector = axisSelectorMap.get(selectorName);
        if (selector == null) {
            throw new NoSuchAxisException("not support axis: " + selectorName);
        }
        return selector;
    }

    public static NodeTest findNodeTestByName(String nodeTestName) {
        NodeTest nodeTest = nodeTestMap.get(nodeTestName);
        if (nodeTest == null) {
            throw new NoSuchFunctionException("not support nodeTest: " + nodeTestName);
        }
        return nodeTest;
    }

    public static Function findFunctionByName(String funcName) {
        Function function = functionMap.get(funcName);
        if (function == null) {
            throw new NoSuchFunctionException("not support function: " + funcName);
        }
        return function;
    }

    public static void registerFunction(Class<? extends Function> func) {
        Function function;
        try {
            function = func.newInstance();
            functionMap.put(function.name(), function);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void registerNodeTest(Class<? extends NodeTest> nodeTestClass) {
        NodeTest nodeTest;
        try {
            nodeTest = nodeTestClass.newInstance();
            nodeTestMap.put(nodeTest.name(), nodeTest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void registerAxisSelector(Class<? extends AxisSelector> axisSelectorClass) {
        AxisSelector axisSelector;
        try {
            axisSelector = axisSelectorClass.newInstance();
            axisSelectorMap.put(axisSelector.name(), axisSelector);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
