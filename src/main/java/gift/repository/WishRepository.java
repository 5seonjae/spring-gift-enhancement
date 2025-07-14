package gift.repository;

import gift.entity.WishItem;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WishRepository {

    private final JdbcClient jdbcClient;

    public WishRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private WishItem mapRowToWishItem(ResultSet rs, int rowNum) throws SQLException {
        return new WishItem(
            rs.getLong("member_id"),
            rs.getLong("product_id"),
            rs.getString("name"),
            rs.getInt("price"),
            rs.getString("image_url"),
            rs.getInt("quantity")
        );
    }

    public List<WishItem> findAllByMemberId(Long memberId) {
        String sql = """
            SELECT wi.member_id, wi.product_id, p.name, p.price, p.image_url, wi.quantity 
            FROM wish_items AS wi 
            JOIN products AS p
            ON wi.product_id = p.id
            WHERE wi.member_id = ?
            """;
        return jdbcClient.sql(sql)
            .param(memberId)
            .query(this::mapRowToWishItem)
            .list();
    }

    public void updateOrInsertWishItem(Long memberId, Long productId, int quantity) {
        String sql = """
            INSERT INTO wish_items (member_id, product_id, quantity)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
            quantity = wish_items.quantity + ?
            """;
        jdbcClient.sql(sql)
            .param(memberId)
            .param(productId)
            .param(quantity)
            .param(quantity)
            .update();
    }

    public int delete(Long memberId, Long productId) {
        String sql = "DELETE FROM wish_items WHERE member_id = ? AND product_id = ?";
        return jdbcClient.sql(sql)
            .param(memberId)
            .param(productId)
            .update();        // update() 가 영향 행 수를 리턴
    }
}