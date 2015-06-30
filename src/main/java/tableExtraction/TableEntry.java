package tableExtraction;

import fragmentsWrapper.QueryWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TableEntry {
    private boolean isLink;
    private String rawContent_;
    private boolean isDbpediaEntity;
    private String link_;



    TableEntry(String link, String raw) {
        isLink = true;
        link_ = link;
        rawContent_ = raw;
    }

    TableEntry(String raw) {
        isLink = false;
        rawContent_ = raw;
    }
    public String getRawContent() {
        return rawContent_;
    }

    private String stripTextBetweenBrackets(String text) {

        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(text);
        while(m.find())
        {
            String substring = m.group(1);
            text = text.replace("["+substring+"]", "");
        }
        return text;
    }

    /*public String getRDFTitle() {
        String content = stripTextBetweenBrackets(tableEntry.toString());
        if (content.contains("(page does not exist)")) {
            return "";
        }
        String title = StringUtils.substringBetween(content, "title=\"", "\"");

        if (title == null) {
            return "";
        }
        title = title.replace(' ', '_');
        return title;
    }*/

    public boolean isLink() {
        return isLink;
    }

    public String getLink() {
        return link_;
    }

    public boolean isDbpediaEntity() {
        QueryWrapper wrapper = new QueryWrapper();
        isDbpediaEntity = wrapper.isLiteralEntityQueryString(this.rawContent_).hasNext();
        return isDbpediaEntity;
    }
}
