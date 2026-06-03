package com.lostfound.utils;

import java.util.regex.Pattern;

/**
 * HTML/富文本 XSS 过滤工具类
 * 用于过滤富文本内容中的危险标签和属性
 */
public class HtmlFilterUtil {

    // 危险标签：直接移除
    private static final Pattern[] DANGEROUS_TAGS = {
            Pattern.compile("<script[^>]*?>[\\s\\S]*?</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<iframe[^>]*?>[\\s\\S]*?</iframe>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<object[^>]*?>[\\s\\S]*?</object>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<embed[^>]*?>[\\s\\S]*?</embed>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<link[^>]*?>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<meta[^>]*?>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<form[^>]*?>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("</form>", Pattern.CASE_INSENSITIVE),
    };

    // 危险事件属性：移除 on* 事件
    private static final Pattern EVENT_ATTR_PATTERN =
            Pattern.compile("\\s+on[a-z]+\\s*=\\s*('[^']*'|\"[^\"]*\"|[^\\s>]*)", Pattern.CASE_INSENSITIVE);

    // javascript 伪协议
    private static final Pattern JAVASCRIPT_PATTERN =
            Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE);

    /**
     * 过滤富文本中的XSS攻击代码
     *
     * @param html 原始富文本内容
     * @return 过滤后的安全内容
     */
    public static String filter(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }

        String result = html;

        // 移除危险标签
        for (Pattern pattern : DANGEROUS_TAGS) {
            result = pattern.matcher(result).replaceAll("");
        }

        // 移除事件属性
        result = EVENT_ATTR_PATTERN.matcher(result).replaceAll("");

        // 移除 javascript 伪协议
        result = JAVASCRIPT_PATTERN.matcher(result).replaceAll("");

        return result;
    }

    /**
     * 过滤用户输入的普通文本（不是富文本）
     * 转义 HTML 特殊字符
     */
    public static String escapeHtml(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
