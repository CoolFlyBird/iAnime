package com.unual.jsoupxpath;

import com.unual.jsoupxpath.antlr.XpathLexer;
import com.unual.jsoupxpath.antlr.XpathParser;
import com.unual.jsoupxpath.core.XValue;
import com.unual.jsoupxpath.core.XpathProcessor;
import com.unual.jsoupxpath.exception.DoFailOnErrorHandler;
import com.unual.jsoupxpath.exception.XpathParserException;
import com.unual.jsoupxpath.exception.XpathSyntaxErrorException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import java.util.LinkedList;
import java.util.List;

public class JXDocument {
    private Elements elements;

    public JXDocument(Elements els) {
        elements = els;
    }

    public static JXDocument create(Document doc) {
        Elements els = doc.children();
        return new JXDocument(els);
    }

    public static JXDocument create(Elements els) {
        return new JXDocument(els);
    }

    public static JXDocument create(String html) {
        Elements els = Jsoup.parse(html).children();
        return new JXDocument(els);
    }

    public static JXDocument createByUrl(String url) {
        Elements els;
        try {
            els = Jsoup.connect(url).get().children();
        } catch (Exception e) {
            throw new XpathParserException("url资源获取失败", e);
        }
        return new JXDocument(els);
    }

    public List<Object> sel(String xpath) {
        List<Object> res = new LinkedList<>();
        for (JXNode node : selN(xpath)) {
            if (node.isElement()) {
                res.add(node.asElement());
            } else {
                res.add(node.toString());
            }
        }
        return res;
    }

    public List<JXNode> selN(String xpath) {
        List<JXNode> finalRes = new LinkedList<>();
        try {
            CharStream input = CharStreams.fromString(xpath);
            XpathLexer lexer = new XpathLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            XpathParser parser = new XpathParser(tokens);
            parser.setErrorHandler(new DoFailOnErrorHandler());
            ParseTree tree = parser.main();
            XpathProcessor processor = new XpathProcessor(elements);
            XValue calRes = processor.visit(tree);
            if (calRes.isElements()) {
                for (Element el : calRes.asElements()) {
                    finalRes.add(JXNode.create(el));
                }
            } else if (calRes.isList()) {
                for (String str : calRes.asList()) {
                    finalRes.add(JXNode.create(str));
                }
            } else if (calRes.isString()) {
                finalRes.add(JXNode.create(calRes.asString()));
            } else if (calRes.isNumber()) {
                finalRes.add(JXNode.create(calRes.asDouble()));
            }
        } catch (Exception e) {
            String msg = "Please check the syntax of your xpath expr or commit a ";
            throw new XpathSyntaxErrorException(msg + ExceptionUtils.getRootCauseMessage(e), e);
        }
        return finalRes;
    }

    public Object selOne(String xpath) {
        JXNode jxNode = selNOne(xpath);
        if (jxNode != null) {
            if (jxNode.isElement()) {
                return jxNode.asElement();
            } else {
                return jxNode.toString();
            }
        }
        return null;
    }

    public JXNode selNOne(String xpath) {
        List<JXNode> jxNodeList = selN(xpath);
        if (jxNodeList != null && jxNodeList.size() > 0) {
            return jxNodeList.get(0);
        }
        return null;
    }
}
