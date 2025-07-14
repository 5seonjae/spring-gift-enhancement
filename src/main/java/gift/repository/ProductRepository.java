package gift.repository;

import gift.entity.Product;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcClient jdbcClient;

    public ProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Product save(Product product) {
        String sql = "INSERT INTO products (name, price, image_url) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient
            .sql(sql)
            .param(product.getName())
            .param(product.getPrice())
            .param(product.getImageUrl())
            .update(keyHolder, "id");

        Long id = keyHolder.getKey().longValue();
        return new Product(id, product.getName(), product.getPrice(), product.getImageUrl());
    }

    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("price"),
            rs.getString("image_url")
        );
        return product;
    }

    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        return jdbcClient
            .sql(sql)
            .query(this::mapRowToProduct)
            .list();
    }

    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        List<Product> result = jdbcClient
            .sql(sql)
            .param(id)
            .query(this::mapRowToProduct)
            .list();
        return result.stream().findFirst();
    }

    public void update(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, image_url = ? WHERE id = ?";
        jdbcClient
            .sql(sql)
            .param(product.getName())
            .param(product.getPrice())
            .param(product.getImageUrl())
            .param(product.getId())
            .update();
    }

    public void delete(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        jdbcClient
            .sql(sql)
            .param(id)
            .update();
    }

    public void deleteAll() {
        String sql = "DELETE FROM products";
        jdbcClient
            .sql(sql)
            .update();
    }
}
