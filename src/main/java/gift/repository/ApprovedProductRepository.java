package gift.repository;

import gift.entity.ApprovedProduct;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ApprovedProductRepository {

    private final JdbcClient jdbcClient;

    public ApprovedProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // 승인 여부 확인
    public boolean isApproved(String name) {
        String sql = "SELECT EXISTS (SELECT 1 FROM approved_products WHERE name = ?)";
        return jdbcClient
            .sql(sql)
            .param(name)
            .query(Boolean.class)
            .single();
    }

    // 승인 상품 등록
    public void save(ApprovedProduct product) {
        String sql = "INSERT INTO approved_products (name) VALUES (?)";
        jdbcClient.sql(sql).param(product.getName()).update();
    }
}
