package per.yan.ding.model.constant;

import java.util.List;

/**
 * 钉钉markdown消息内容格式工具类
 *
 * @author gaoyan
 * @date 2019/2/20 11:16
 */
public class DDMarkdownMsgTextUtil {

    /**
     * 加粗
     */
    public static String getBoldText(String text) {
        return "**" + text + "**";
    }

    /**
     * 斜体
     */
    public static String getItalicText(String text) {
        return "*" + text + "*";
    }

    /**
     * 链接
     */
    public static String getLinkText(String text, String href) {
        return "[" + text + "](" + href + ")";
    }

    /**
     * 图片
     */
    public static String getImageText(String imageUrl) {
        return "![image](" + imageUrl + ")";
    }

    /**
     * 标题分级
     */
    public static String getHeaderText(int headerType, String text) {
        if (headerType < 1 || headerType > 6) {
            throw new IllegalArgumentException("headerType should be in [1, 6]");
        }

        StringBuffer numbers = new StringBuffer();
        for (int i = 0; i < headerType; i++) {
            numbers.append("#");
        }
        return numbers + " " + text;
    }

    /**
     * 引用
     */
    public static String getReferenceText(String text) {
        return "> " + text;
    }

    /**
     * 有序列表
     */
    public static String getOrderListText(List<String> orderItem) {
        if (orderItem.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= orderItem.size() - 1; i++) {
            sb.append(String.valueOf(i) + ". " + orderItem.get(i - 1) + "\n");
        }
        sb.append(String.valueOf(orderItem.size()) + ". " + orderItem.get(orderItem.size() - 1));
        return sb.toString();
    }

    /**
     * 无序列表
     */
    public static String getUnorderListText(List<String> unorderItem) {
        if (unorderItem.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < unorderItem.size() - 1; i++) {
            sb.append("- " + unorderItem.get(i) + "\n");
        }
        sb.append("- " + unorderItem.get(unorderItem.size() - 1));
        return sb.toString();
    }

    /**
     * 换行符
     */
    public static String newLine() {
        return "\n";
    }
}
