package gift;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.WishItem;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WishRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishRepository wishRepository;

    @Test
    @DisplayName("findAllByMemberId: 해당 회원의 찜 목록 조회")
    void findAllByMemberId_shouldReturnItems_forMember() {
        // given
        Member m = memberRepository.save(new Member("u1@example.com", "password"));
        Product p = productRepository.save(new Product("P1", 1000, "http://example.com/img.png"));
        WishItem wi = new WishItem(m, p, 3);
        wishRepository.save(wi);

        // when
        List<WishItem> list = wishRepository.findAllByMemberId(m.getId());

        // then
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getProduct().getName()).isEqualTo("P1");
        assertThat(list.getFirst().getProduct().getPrice()).isEqualTo(10);
        assertThat(list.getFirst().getProduct().getImageUrl()).isEqualTo("http://example.com/img.png");
        assertThat(list.getFirst().getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("deleteByMemberIdAndProductId: 해당 항목 삭제 후 영향 개수 반환")
    void deleteByMemberIdAndProductId_shouldDeleteAndReturnCount() {
        // given
        Member m = memberRepository.save(new Member("u2@example.com", "password"));
        Product p = productRepository.save(new Product("P2", 2000, "http://example.com/img.png"));
        WishItem wi = new WishItem(m, p, 5);
        wishRepository.save(wi);

        // when
        int deleted = wishRepository.deleteByMemberIdAndProductId(m.getId(), p.getId());

        // then
        assertThat(deleted).isEqualTo(1);
        assertThat(wishRepository.findAllByMemberId(m.getId())).isEmpty();
    }
}
