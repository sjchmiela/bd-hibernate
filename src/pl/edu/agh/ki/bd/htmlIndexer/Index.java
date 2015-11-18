package pl.edu.agh.ki.bd.htmlIndexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.edu.agh.ki.bd.htmlIndexer.model.ProcessedUrl;
import pl.edu.agh.ki.bd.htmlIndexer.model.Sentence;
import pl.edu.agh.ki.bd.htmlIndexer.model.Word;
import pl.edu.agh.ki.bd.htmlIndexer.persistence.HibernateUtils;

public class Index 
{
	// jboss multiple transactions – 15,477 ms, single transaction – 9685 ms
	public void indexWebPage(String url) throws IOException
	{
		
		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.body().select("*");
        Session session = HibernateUtils.getSession();
        Transaction transaction = session.beginTransaction();
        ProcessedUrl processedUrl = new ProcessedUrl(url);
        session.persist(processedUrl);
		for (Element element : elements) 
		{
			if (element.ownText().trim().length() > 1)
			{
				for (String sentenceContent : element.ownText().split("\\. ")) {
                    Sentence sentence = new Sentence(processedUrl);
                    session.persist(sentence);

                    List<String> sentenceWords = Arrays.asList(sentenceContent.split("\\s+"));
                    List<Word> wordsThatAreAlreadyInDatabaseList = session.createQuery("select w from Word w where w.content in :words").setParameterList("words", sentenceWords).list();

                    HashMap<String, Word> wordsThatAreAlreadyInDatabase = new HashMap<>();
                    for(Word word : wordsThatAreAlreadyInDatabaseList) {
                        wordsThatAreAlreadyInDatabase.put(word.getContent(), word);
                    }

//                    session.persist(sentence);

                    for(String wordContent : sentenceWords) {
                        if(wordsThatAreAlreadyInDatabase.containsKey(wordContent)) {
                            System.out.println("adding to " + wordContent);
                            Word word = wordsThatAreAlreadyInDatabase.get(wordContent);
                            System.out.println(word.getSentences());
                            sentence.getWords().add(word);
                        } else {
                            System.out.println("creating " + wordContent);
                            Word word = new Word(wordContent);
                            session.persist(word);
                            word.getSentences().add(sentence);
                            sentence.getWords().add(word);
                            wordsThatAreAlreadyInDatabase.put(wordContent, word);
                        }
                    }
                    session.persist(sentence);
                    for(String wordContent : wordsThatAreAlreadyInDatabase.keySet()) {
                        session.persist(wordsThatAreAlreadyInDatabase.get(wordContent));
                    }

//                    session.merge(sentence);
                }
			}
		}
        transaction.commit();
        session.close();
	}	
	
	public List<Sentence> findSentencesByWords(String words)
	{
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
        System.out.println("SEARCHING: "+words.split("\\s+")[0]);
        List<Sentence> result = session.createQuery("select s from Sentence s join fetch s.processedUrl join fetch s.words").list();

		transaction.commit();
		session.close();
		
		return result;
	}

    public List<String> findSentencesByLengthMoreThan(Integer minLength)
    {
        Session session = HibernateUtils.getSession();
        Transaction transaction = session.beginTransaction();

        List<String> result = session.createQuery("select s.content from Sentence s where length(s.content) >= :minLength").setParameter("minLength", minLength).list();

        transaction.commit();
        session.close();

        return result;
    }

    public List<Object[]> statistics() {
        Session session = HibernateUtils.getSession();
        Transaction transaction = session.beginTransaction();

        List<Object[]> result = session.createQuery("select p.url, count(elements(p.sentences)) from ProcessedUrl p group by p.id").list();

        transaction.commit();
        session.close();

        return result;
    }
}
