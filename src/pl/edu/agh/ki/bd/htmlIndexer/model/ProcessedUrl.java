package pl.edu.agh.ki.bd.htmlIndexer.model;

import java.util.Date;
import java.util.Set;

/**
 * Created by sjchmiela on 04.11.15.
 */
public class ProcessedUrl {
    private long id;
    private Date date;
    private String url;
    private Set<Sentence> sentences;

    public ProcessedUrl() {}

    public ProcessedUrl(String url)
    {
        this.setUrl(url);
        this.setDate(new Date());
    }

    public Set<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(Set<Sentence> sentences) {
        this.sentences = sentences;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return getUrl() + " processed on " + getDate().toString();
    }
}
