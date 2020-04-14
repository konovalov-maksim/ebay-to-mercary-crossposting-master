package gui;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.*;

public class WebViewCookieStore implements CookieStore {

    private Map<String, List<HttpCookie>> allCookies = new HashMap<>();
    private List<URI> uries = new ArrayList<>();

    @Override
    public synchronized void add(URI uri, HttpCookie newCookie) {
        uries.add(uri);
        String domain = uri.getHost();
        List<HttpCookie> domainCookies = allCookies.getOrDefault(domain, new LinkedList<>());
        domainCookies.removeIf(c -> c.getName().equals(newCookie.getName()));
        domainCookies.add(newCookie);
        allCookies.put(domain, domainCookies);
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        return get(uri.getHost());
    }

    public synchronized List<HttpCookie> get(String domain) {
        return allCookies.getOrDefault(domain, new LinkedList<>());
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> allCookiesList = new ArrayList<>();
        for (Map.Entry<String, List<HttpCookie>> entry : allCookies.entrySet()) {
            allCookiesList.addAll(entry.getValue());
        }
        return allCookiesList;
    }

    @Override
    public synchronized List<URI> getURIs() {
        return uries;
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        String domain = uri.getHost();
        return allCookies.get(domain).remove(cookie);
    }

    @Override
    public synchronized boolean removeAll() {
        uries.clear();
        allCookies.clear();
        return true;
    }
}
