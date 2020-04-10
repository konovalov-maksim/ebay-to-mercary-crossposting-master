package core.ebayLoader;

import core.Item;

public interface LoadingListener {
    void onItemInfoLoaded(Item item);
    void onItemImagesLoaded(Item item);
    void onAllItemsLoaded();
}
