package gift.entity;

public class ApprovedProduct {

    private Long id;
    private String name;

    public ApprovedProduct() {
    }

    public ApprovedProduct(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
