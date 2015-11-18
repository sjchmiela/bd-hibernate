package pl.edu.agh.ki.bd.htmlIndexer.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sjchmiela on 16.11.2015.
 */
public class Word {
    private String content;
    private Set<Sentence> sentences;

    public Word() {}
    public Word(String content) {
        this.content = content;
        this.sentences = new HashSet<>();
    }

    public Set<Sentence> getSentences() {
        return sentences;
    }
    public void setSentences(Set<Sentence> sentences) {
        this.sentences = sentences;
    }

    public void addSentence(Sentence sentence) {
        this.sentences.add(sentence);
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

}
