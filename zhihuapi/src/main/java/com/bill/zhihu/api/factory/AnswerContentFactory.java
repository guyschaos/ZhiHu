package com.bill.zhihu.api.factory;

import android.text.TextUtils;

import com.bill.zhihu.api.R;
import com.bill.zhihu.api.ZhihuApi;
import com.bill.zhihu.api.bean.AnswerContent;
import com.bill.zhihu.api.utils.ZhihuURL;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

/**
 * 解析答案页面
 * Created by Bill-pc on 2015/7/4.
 */
public class AnswerContentFactory {

    private static final String TAG = "AnswerContentFactory";

    private AnswerContent answerContent;

    private Elements elements;

    public AnswerContentFactory(Elements elements) {
        this.elements = elements;
    }

    public AnswerContent create() {
        answerContent = new AnswerContent();

        //aid 和 answer_id
        answerContent.setAnswerId(elements.attr("data-atoken"));
        answerContent.setAid(elements.attr("data-aid"));

        //赞同票
        Elements voter = elements.select("div[class=zm-votebar]");
        String voteCount = voter.select("span[class=count]").text();
        answerContent.setVote(voteCount);

        //答主主页url
        Elements peopleUrl = elements.select("a[class=zm-item-link-avatar]");
        String href = peopleUrl.attr("href");
        answerContent.setPeopleUrl(ZhihuURL.HOST + href);

        //答主姓名
        if (TextUtils.isEmpty(href)) {
            // 匿名用户
            answerContent.setPeopleName(ZhihuApi.getContext().getResources().getString(R.string.anonymous));
        } else {
            // 正常用户
            Elements people = elements.select("a[href=" + href + "]");
            answerContent.setPeopleName(people.text());
        }

        //答主头像
        Elements avatarUrlElement = elements.select("img[class=zm-list-avatar]");
        answerContent.setAvatarImgUrl("http:" + avatarUrlElement.attr("src").replace("_s", "_m"));

        // 简介
        Elements introElement = elements.select("strong[class=zu-question-my-bio]");
        answerContent.setIntro(introElement.attr("title"));

        //发布日期 编辑日期
        Elements postDateElements = elements
                .select("a[class^=answer-date-link]");
        if (postDateElements.hasAttr("data-tip")) {
            //编辑日期
            answerContent.setEditDate(postDateElements.attr("data-tip").replace("s$t$",
                    ""));
        }
        answerContent.setPostDate(postDateElements.text());

        //答案内容 添加一个样式表显示美观，样式表是从知乎网站上copy下来的
        Attribute relAttr = new Attribute("rel", "stylesheet");
        Attribute hrefAttr = new Attribute("href", "answer_style.css");
        Attribute typeAttr = new Attribute("type", "text/css");
        Attributes attrs = new Attributes();
        attrs.put(relAttr);
        attrs.put(hrefAttr);
        attrs.put(typeAttr);
        Element styleSheetElement = new Element(Tag.valueOf("link"), "", attrs);
        Elements answerElement = elements.select("div[class=zm-item-rich-text]>div");
        answerElement.prepend(styleSheetElement.toString());
        answerElement.select("img[class$=lazy]").remove();//去掉img造成的空白图片，webview中加载的是noscript标签中的
        // 加入发布和编辑日期
        Attribute postCssAttr = new Attribute("class", "post-date meta-item");
        Attributes postAttrs = new Attributes();
        postAttrs.put(postCssAttr);
        Element postDateElement = new Element(Tag.valueOf("a"), "", postAttrs);
        postDateElement.text(answerContent.getPostDate());
        // 让正文和日期隔开几行
        answerElement.append("<br>");
        answerElement.append("<br>");
        answerElement.append(postDateElement.toString());
        if (answerContent.getEditDate() != null && !answerContent.getEditDate().isEmpty()) {
            Attribute editCssAttr = new Attribute("class", "post-date meta-item");
            Attributes editAttrs = new Attributes();
            editAttrs.put(editCssAttr);
            Element editDateElement = new Element(Tag.valueOf("a"), "", editAttrs);
            editDateElement.text(answerContent.getEditDate());
            answerElement.append("<br>");
            answerElement.append(editDateElement.toString());
        }

        answerContent.setAnswer(answerElement.toString());

        return answerContent;
    }
}