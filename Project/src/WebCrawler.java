import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * @author stewartpowell
 *
 */
public class WebCrawler {
    /** the thread safe inverted index */
    private ThreadedInvertedIndex index;
    /** the workqueue used to designate URLS */
    private final WorkQueue queue;

    /**
     * Constructor for WebCrawler
     * 
     * @param index the ThreadSafe inverted index
     * @param queue the work queue used to designate tasks
     */
    public WebCrawler(ThreadedInvertedIndex index, WorkQueue queue) {
        this.index = index;
        this.queue = queue;
    }

    /**
     * Builds an inverted index from the seed URL
     * 
     * @param url       the base URL to start the web crawl
     * @param redirects the number of redirects allowed
     */
    public void crawlWeb(String url, int redirects) {
        try {
            Map<String, List<String>> headers = HttpsFetcher.fetchURL(url);

        } catch (MalformedURLException e) {
            System.out.println("Invalid URL from given parameter: " + url);
        } catch (IOException e) {
            System.out.println("IOexception incurred in crawlWeb function of WebCrawler.");
        }

    }

}
