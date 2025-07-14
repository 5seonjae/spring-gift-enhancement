package gift;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.WishItem;
import gift.repository.WishRepository;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@JdbcTest
@Import(WishRepository.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private JdbcClient jdbcClient;

    Long memberId;
    Long chocolateId;
    Long candyId;

    @BeforeEach
    void setUp() {
        KeyHolder memberKeyHolder = new GeneratedKeyHolder();
        KeyHolder productKeyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("""
                INSERT INTO members (email, password, is_admin)
                VALUES (?, ?, ?)
                """).param(1, "user@example.com")
            .param(2, "userPassword")
            .param(false)
            .update(memberKeyHolder, "id");

        memberId = memberKeyHolder.getKey().longValue();

        jdbcClient.sql("""
            INSERT INTO products (name, price, image_url)
            VALUES (?, ?, ?)
            """).param(1, "초콜릿")
            .param(2, "1500")
            .param(3, "https://www.chocolate.png")
            .update(productKeyHolder, "id");

        chocolateId = productKeyHolder.getKey().longValue();

        jdbcClient.sql("""
            INSERT INTO products (name, price, image_url)
            VALUES (?, ?, ?)
            """).param(1, "사탕")
            .param(2, "500")
            .param(3, "https://www.candy.png")
            .update(productKeyHolder, "id");

        candyId = productKeyHolder.getKey().longValue();

        jdbcClient.sql("""
            INSERT INTO wish_items (member_id, product_id, quantity)
            VALUES (?, ?, ?)
            """).param(1, memberId)
            .param(2, chocolateId)
            .param(3, 1)
            .update();
    }

    @Test
    @DisplayName("memberId 로 wishlist 목록 조회")
    void findAllByMemberId() {
        List<WishItem> list = wishRepository.findAllByMemberId(memberId);

        assertThat(list)
            .hasSize(1)
            .first()
            .satisfies(item -> {
                assertThat(item.getMemberId()).isEqualTo(memberId);
                assertThat(item.getProductId()).isEqualTo(chocolateId);
                assertThat(item.getProductName()).isEqualTo("초콜릿");
                assertThat(item.getPrice()).isEqualTo(1500);
                assertThat(item.getImageUrl()).isEqualTo("https://www.chocolate.png");
                assertThat(item.getQuantity()).isEqualTo(1);
            });
    }

    @Test
    @DisplayName("없는 항목은 INSERT, 있으면 수량 누적 업데이트")
    void updateOrInsertWishItem() {
        // 위시 리스트에 없는 새 상품(사탕) 추가 → INSERT
        wishRepository.updateOrInsertWishItem(memberId, candyId, 2);
        // 같은 상품(사탕) 다시 추가 → UPDATE (2 → 5)
        wishRepository.updateOrInsertWishItem(memberId, candyId, 3);

        int qty = wishRepository.findAllByMemberId(memberId)
            .stream()
            .filter(wi -> Objects.equals(wi.getProductId(), candyId))
            .findFirst()
            .orElseThrow()
            .getQuantity();

        assertThat(qty).isEqualTo(5);
    }

    @Test
    @DisplayName("memberId + productId 로 wishlist 항목 삭제")
    void deleteWishItem() {
        int affected = wishRepository.delete(memberId, chocolateId);

        assertThat(affected).isEqualTo(1);          // 삭제된 행 수
        assertThat(wishRepository.findAllByMemberId(memberId)).isEmpty();
    }
}
