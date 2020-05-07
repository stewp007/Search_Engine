import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author stewartpowell
 *
 */
public class WebCrawler {
    /** the thread safe inverted index */
    private ThreadedInvertedIndex index;

    /** the workqueue used to designate URLS */
    private final WorkQueue queue;

    /** The set of links used for webcrawling */
    private final Set<URL> links;

    /** The number of links allowed to parse */
    private int limit;

    /** The default stemmer algorithm used by this class. */
    public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

    /**
     * Constructor for WebCrawler
     * 
     * @param index the ThreadSafe inverted index
     * @param queue the work queue used to designate tasks
     * @param limit the number of links allowed to parse
     */
    public WebCrawler(ThreadedInvertedIndex index, WorkQueue queue, int limit) {
        this.index = index;
        this.queue = queue;
        this.limit = limit;
        this.links = new HashSet<>();
    }

    /**
     * Builds an inverted index from the seed URL
     * 
     * @param url the base URL to start the web crawl
     */
    public void crawlWeb(String url) {
        try {
            URL link = new URL(url);
            links.add(link);
            queue.execute(new UrlBuilder(link));
            queue.finish();
            links.clear();
        } catch (MalformedURLException e) {
            System.out.println("Unble to convert string to URL.");
        }
    }

    /**
     * Adds a string of html to an InvertedIndex
     * 
     * @param index the invertedIndex being added to
     * @param link  the link of the html
     * @param html  the string of html from the webpage
     */
    public static void htmlToIndex(InvertedIndex index, URL link, String html) {
        SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
        String[] allStems = TextParser.parse(html);
        int linePosition = 0;
        for (String word : allStems) {
            linePosition++;
            String stemmedWord = stemmer.stem(word).toString();
            index.add(stemmedWord, link.toString(), linePosition);
        }
    }

    /**
     * @author stewartpowell
     *
     */
    private class UrlBuilder implements Runnable {

        /**
         * the link used for webcrawl
         */
        private final URL link;

        /**
         * the number of redirects allowed
         */
        private final int redirects = 3;

        /**
         * @param link the link used for webcrawl
         */
        private UrlBuilder(URL link) {
            this.link = link;
        }

        @Override
        public void run() {

            String html = HtmlFetcher.fetch(link, redirects);
            if (html == null) {
                return;
            }
            html = HtmlCleaner.stripBlockElements(html);
            ArrayList<URL> listLinks = LinkParser.listLinks(link, html);
            html = HtmlCleaner.stripTags(html);
            html = HtmlCleaner.stripEntities(html);
            InvertedIndex local = new InvertedIndex();
            htmlToIndex(local, link, html);
            index.addAll(local);

            synchronized (links) {
                for (URL url : listLinks) {
                    if (links.size() < limit && !links.contains(url)) {
                        links.add(url);
                        queue.execute(new UrlBuilder(url));
                    }
                }
            }

        }

    }

}
