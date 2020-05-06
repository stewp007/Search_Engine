import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A specialized version of {@link HttpsFetcher} that follows redirects and
 * returns HTML content if possible.
 *
 * @see HttpsFetcher
 */
public class HtmlFetcher {

    /**
     * Returns {@code true} if and only if there is a "Content-Type" header and the
     * first value of that header starts with the value "text/html"
     * (case-insensitive).
     *
     * @param headers the HTTP/1.1 headers to parse
     * @return {@code true} if the headers indicate the content type is HTML
     */
    public static boolean isHtml(Map<String, List<String>> headers) {
        if (headers.containsKey("Content-Type")) {
            if (headers.get("Content-Type").get(0).toLowerCase().startsWith("text/html")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses the HTTP status code from the provided HTTP headers, assuming the
     * status line is stored under the {@code null} key.
     *
     * @param headers the HTTP/1.1 headers to parse
     * @return the HTTP status code or -1 if unable to parse for any reasons
     */
    public static int getStatusCode(Map<String, List<String>> headers) {
        try {
            return Integer.parseInt(headers.get(null).get(0).replaceAll("(HTTP\\/1\\.1 )", "").replaceAll(" \\D+", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns {@code true} if and only if the HTTP status code is between 300 and
     * 399 (inclusive) and there is a "Location" header with at least one value.
     *
     * @param headers the HTTP/1.1 headers to parse
     * @return {@code true} if the headers indicate the content type is HTML
     */
    public static boolean isRedirect(Map<String, List<String>> headers) {
        if (headers.containsKey("Location")) {
            if (headers.get("Location").size() > 0 && getStatusCode(headers) < 400 || getStatusCode(headers) >= 300) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all the HTTP(S) links found in the href attribute of the
     * anchor tags in the provided HTML. The links will be converted to absolute
     * using the base URL and cleaned (removing fragments and encoding special
     * characters as necessary).
     *
     * @param html the raw html associated with the base url
     * @return cleaned list of all http(s) links in the order they were found
     */
    public static ArrayList<URL> listLinks(String html) {
        ArrayList<URL> listURLS = new ArrayList<URL>();
        Pattern p = Pattern.compile("(?i)<a\\s*?(?:[^>]*?\\s+)?\\W*?href\\W*?=\\W*?([\"'])(.*?)\\1",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        while (m.find()) {
            String found = m.group(2);
            try {
                URL link = new URL(found);
                listURLS.add(link);
            } catch (MalformedURLException e) {
            }
        }

        return listURLS;
    }

    /**
     * Fetches the resource at the URL using HTTP/1.1 and sockets. If the status
     * code is 200 and the content type is HTML, returns the HTML as a single
     * string. If the status code is a valid redirect, will follow that redirect if
     * the number of redirects is greater than 0. Otherwise, returns {@code null}.
     *
     * @param url       the url to fetch
     * @param redirects the number of times to follow redirects
     * @return the html or {@code null} if unable to fetch the resource or the
     *         resource is not html
     *
     * @see HttpsFetcher#openConnection(URL)
     * @see HttpsFetcher#printGetRequest(PrintWriter, URL)
     * @see HttpsFetcher#getHeaderFields(BufferedReader)
     * @see HttpsFetcher#getContent(BufferedReader)
     *
     * @see String#join(CharSequence, CharSequence...)
     *
     * @see #isHtml(Map)
     * @see #isRedirect(Map)
     */
    public static String fetch(URL url, int redirects) {
        Map<String, List<String>> map;
        try {
            map = HttpsFetcher.fetchURL(url);
            String joined = String.join("\n", map.get("Content"));
            if (getStatusCode(map) >= 200 && getStatusCode(map) < 300) {
                if (isHtml(map)) {
                    return joined;
                }
            } else {
                if (getStatusCode(map) >= 300 && getStatusCode(map) < 400 && redirects > 0) {
                    List<URL> links = listLinks(joined);
                    for (URL link : links) {
                        return fetch(link, --redirects);
                    }
                }
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Converts the {@link String} url into a {@link URL} object and then calls
     * {@link #fetch(URL, int)}.
     *
     * @param url       the url to fetch
     * @param redirects the number of times to follow redirects
     * @return the html or {@code null} if unable to fetch the resource or the
     *         resource is not html
     *
     * @see #fetch(URL, int)
     */
    public static String fetch(String url, int redirects) {
        try {
            return fetch(new URL(url), redirects);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Converts the {@link String} url into a {@link URL} object and then calls
     * {@link #fetch(URL, int)} with 0 redirects.
     *
     * @param url the url to fetch
     * @return the html or {@code null} if unable to fetch the resource or the
     *         resource is not html
     *
     * @see #fetch(URL, int)
     */
    public static String fetch(String url) {
        return fetch(url, 0);
    }

    /**
     * Calls {@link #fetch(URL, int)} with 0 redirects.
     *
     * @param url the url to fetch
     * @return the html or {@code null} if unable to fetch the resource or the
     *         resource is not html
     */
    public static String fetch(URL url) {
        return fetch(url, 0);
    }
}
