package core.ebayLoader.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SellerCategory {

    private final String name;

    private final List<SellerItem> sellerItems = new ArrayList<>();

    public SellerCategory(String name) {
        this.name = name;
    }

    public void addSellerItem(SellerItem sellerItem) {
        sellerItems.add(sellerItem);
    }

    public Integer getItemsCount() {
        return sellerItems.size();
    }

    public String getName() {
        return name;
    }

    public List<SellerItem> getSellerItems() {
        return sellerItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SellerCategory that = (SellerCategory) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
