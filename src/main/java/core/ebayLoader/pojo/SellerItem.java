package core.ebayLoader.pojo;

public class SellerItem {

    private final String id;

    private final String categoryName;

    public SellerItem(String id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public String getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

}
