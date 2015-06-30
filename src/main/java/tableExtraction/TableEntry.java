package tableExtraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TableEntry {
    private boolean isLink;
    private String textContent;
    private boolean isDbpediaEntity;


    TableEntry(String link, String raw) {

    }

    public String getTextContent() {
        return textContent;
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

    private boolean checkDbpediaEntity() {
        SPARQLHelper helper = new SPARQLHelper();
        return helper.isDbpediaEntity(this);
    }
}
