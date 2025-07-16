package gift;

import gift.entity.Product;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("save & findById: 저장된 상품을 조회할 수 있다")
    void save_and_findById_shouldWork() {
        // given
        Product p = new Product("Chocolate", 1000, "http://example.com/img.png");
        productRepository.save(p);

        // when
        Product found = productRepository.findById(p.getId()).orElseThrow();

        // then
        assertThat(found.getName()).isEqualTo("Chocolate");
        assertThat(found.getPrice()).isEqualTo(1000);
        assertThat(found.getImageUrl()).isEqualTo("http://example.com/img.png");
    }

    @Test
    @DisplayName("findAll: 저장된 상품 리스트를 모두 반환")
    void findAll_shouldReturnAllSaved() {
        productRepository.save(new Product("A", 1, "http://exampleA.com/img.png"));
        productRepository.save(new Product("B", 2, "http://exampleB.com/img.png"));

        List<Product> all = productRepository.findAll();
        assertThat(all).hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("A", "B");
        assertThat(all).hasSize(2)
                .extracting(Product::getPrice)
                .containsExactlyInAnyOrder(1, 2);
        assertThat(all).hasSize(2)
                .extracting(Product::getImageUrl)
                .containsExactlyInAnyOrder("http://exampleA.com/img.png", "http://exampleB.com/img.png");
    }
}
