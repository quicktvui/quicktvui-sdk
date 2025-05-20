package com.quicktvui.base.ui.tag;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlTagHandler implements Html.TagHandler, Html.ImageGetter, ContentHandler {

    private static final String LOG_TAG = "HtmlTagHandler";

    private final String H5_TAG = "html";  //自定义标签，该标签无法在原Html类中解析
    private volatile ContentHandler orginalContentHandler;
    private int count = 0;  //防止自定义的相互嵌套的情况 如：<html><html></html></html>
    //设置标签计数器，防止自定义标签嵌套自定义标签
    private XMLReader originalXmlReader;
    private Editable originlaEditableText;  //该对象是SpannableStringBuilder
    private List<String> orginalTags = null;

    //自定义解析器集合
    private final Map<String, HtmlTag> tagHandlerMap;


    public HtmlTagHandler() {
        String orginalContentHandlerTag = "br|p|ul|li|div|span|strong|b|em|cite|dnf|i|big|small|font|blockquote|tt|a|u|del|s|strike|sup|sub|h1|h2|h3|h4|h5|h6|img";
//原android.text.Html类中可以解析的标签
        orginalTags = Arrays.asList(orginalContentHandlerTag.split("|"));
        tagHandlerMap = new HashMap<>();
    }

    //注册解析器
    public void registerTag(String tagName, HtmlTag tagHandler) {
        tagHandlerMap.put(tagName, tagHandler);
    }

    public HtmlTag unregisterTag(String tagName) {
        return tagHandlerMap.remove(tagName);
    }

    @Override
    public Drawable getDrawable(String source) {
        return null;
    }

    //处理原Html中无法识别的标签
    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (opening) {
            startHandleTag(tag, output, xmlReader);
        } else {
            endHandleTag(tag, output, xmlReader);
        }

    }

    private void startHandleTag(String tag, Editable output, XMLReader xmlReader) {

        if (tag.equalsIgnoreCase(H5_TAG)) {
            if (orginalContentHandler == null) {
                orginalContentHandler = xmlReader.getContentHandler();
                this.originalXmlReader = xmlReader; //获取XmlReader
                this.originalXmlReader.setContentHandler(this);//获取控制权,让本类监听解析流程
                this.originlaEditableText = output;  //获取到SpannableStringBuilder

            }
            count++;
        }

    }

    private void endHandleTag(String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase(tag)) {
            count--;
            if (count == 0) {
                this.originalXmlReader.setContentHandler(this.orginalContentHandler);
                //将原始的handler交还
                this.originalXmlReader = null;
                this.originlaEditableText = null;
                this.orginalContentHandler = null;
                //还原控制权
            }
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        if (localName.equalsIgnoreCase(H5_TAG)) {
            handleTag(true, localName, this.originlaEditableText, this.originalXmlReader);
        } else if (canHandleTag(localName)) {  //拦截，判断是否可以解析该标签

            final HtmlTag htmlTag = tagHandlerMap.get(localName);  //读取自定义解析器开始解析
            htmlTag.startHandleTag(this.originlaEditableText, atts);

        } else if (orginalTags.contains(localName)) { //无法解析的优先让原Html类解析
            this.orginalContentHandler.startElement(uri, localName, qName, atts);
        } else {
//            Log.e(LOG_TAG, "无法解析的标签<" + localName + ">");
        }

    }

    private boolean canHandleTag(String tagName) {
        if (!tagHandlerMap.containsKey(tagName)) {
            return false;
        }
        final HtmlTag htmlTag = tagHandlerMap.get(tagName);
        return htmlTag != null;

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (localName.equalsIgnoreCase(H5_TAG)) {
            handleTag(false, localName, this.originlaEditableText, this.originalXmlReader);
        } else if (canHandleTag(localName)) {
            final HtmlTag htmlTag = tagHandlerMap.get(localName); //读取自定义解析器结束解析
            htmlTag.endHandleTag(this.originlaEditableText);
        } else if (orginalTags.contains(localName)) {
            this.orginalContentHandler.endElement(uri, localName, qName);
        } else {
//            Log.e(LOG_TAG, "无法解析的标签</" + localName + ">");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        orginalContentHandler.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        orginalContentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        orginalContentHandler.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        orginalContentHandler.skippedEntity(name);
    }


    @Override
    public void setDocumentLocator(Locator locator) {
        orginalContentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        orginalContentHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        orginalContentHandler.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        orginalContentHandler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        orginalContentHandler.endPrefixMapping(prefix);
    }


}
