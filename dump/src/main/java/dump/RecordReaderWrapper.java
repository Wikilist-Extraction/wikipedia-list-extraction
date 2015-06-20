package dump;

import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nico on 19/06/15.
 */
public class RecordReaderWrapper {

    private RecordReader<Article> reader;
    private RecordReader<Article> listReader;

    public RecordReaderWrapper(String filename) {
        reader = new RecordReader<Article>(filename, new JsonRecordParser<Article>(Article.class));
        listReader = reader.filter(new TypeFilter(Article.Type.LIST));
    }

    public List<Article> getArticlesList() {
        List<Article> articleList = new ArrayList<Article>();

        for (Article article : listReader) {
            articleList.add(article);
        }
        return articleList;
    }
}
