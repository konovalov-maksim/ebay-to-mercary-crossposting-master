package core;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class EbayScraper {

    private final long timeout = 10000;
    private final String TOKEN;
    private int threads = 1;
    private boolean isRunning = false;

    private Deque<String> unprocessed = new ConcurrentLinkedDeque<>();

    private List<Item> items = new ArrayList<>();

    public EbayScraper(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public void addItemsIds(List<String> itemsIds) {
        this.unprocessed.addAll(itemsIds);
    }


}
