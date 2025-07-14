package gift.entity;

public class WishItem {

    private final Long memberId;
    private final Long productId;
    private final String productName;
    private final int price;
    private final String imageUrl;
    private final int quantity;

    public WishItem(Long memberId,
                    Long productId,
                    String productName,
                    int price,
                    String imageUrl,
                    int quantity) {
        this.memberId = memberId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }
}
