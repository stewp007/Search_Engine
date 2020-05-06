import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author stewartpowell
 *
 */
public class WebCrawler {
    /** the thread safe inverted index */
    private ThreadedInvertedIndex index;
    /** the workqueue used to designate URLS */
    private final WorkQueue queue;

    private final Set<URL> links;
    /** The number of links allowed to parse */
    private int limit;

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
     * @param url       the base URL to start the web crawl
     * @param redirects the number of redirects allowed
     * @throws MalformedURLException
     */
    public void crawlWeb(String url) throws MalformedURLException {
        URL link = new URL(url);
        links.add(link);
        queue.execute(new UrlBuilder(link));
        queue.finish();
        links.clear();
    }

    private class UrlBuilder implements Runnable {

        private final URL link;

        private final int redirects = 3;

        private UrlBuilder(URL link) {
            this.link = link;
        }

        @Override
        public void run() {
            String html = HtmlFetcher.fetch(link, redirects);
            if (html != null) {

            }

        }

    }

}
