package tableExtraction;

import fragmentsWrapper.QueryWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TableEntry {
    private boolean isLink;
    private String rawContent_;
    private boolean isDbpediaEntity;
    private String link_;
    private boolean checkedDpbediaEntity = false;



    public TableEntry(String link, String raw) {
        isLink = true;
        link_ = link;
        rawContent_ = raw;
    }

    public TableEntry(String raw) {
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
        if (link_ == null && isDbpediaEntity) {
            return  rawContent_.replace(" ", "_");
        }
        return link_;
    }

    public boolean isDbpediaEntity() {
        if (!checkedDpbediaEntity) {
            QueryWrapper wrapper = new QueryWrapper();
            if (isLink) {
                isDbpediaEntity = wrapper.isLiteralEntityQueryString(this.getLink());
            } else {
                isDbpediaEntity = wrapper.isLiteralEntityQueryString(this.rawContent_);
            }
            checkedDpbediaEntity = true;
        }
        return isDbpediaEntity;
    }
}
