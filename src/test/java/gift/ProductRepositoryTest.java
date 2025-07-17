package gift;

import gift.entity.Product;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
        // given — 기존 데이터 삭제 후 3개 상품 저장
        productRepository.deleteAll();
        productRepository.saveAll(List.of(
                new Product("A", 1, "http://exampleA.com/img.png"),
                new Product("B", 2, "http://exampleB.com/img.png"),
                new Product("C", 3, "http://exampleC.com/img.png")
        ));

        // when — 페이지 크기 1, id 내림차순 정렬
        Pageable pageable = PageRequest.of(
                0,                             // page number
                1,                                        // page size
                Sort.by("id").descending()      // sort by id desc
        );
        // 첫 번째 페이지 조회
        Page<Product> page0 = productRepository.findAll(pageable);

        // then — 메타데이터 검증
        assertThat(page0.getTotalElements()).isEqualTo(3);     // 전체 요소 수
        assertThat(page0.getTotalPages()).isEqualTo(3);        // 전체 페이지 수
        assertThat(page0.getNumber()).isZero();                        // 현재 페이지 인덱스
        assertThat(page0.getSize()).isEqualTo(1);              // 페이지 크기

        // then — 첫 번째 페이지 콘텐츠 검증
        assertThat(page0.getContent())
                .extracting(Product::getName)
                .containsExactly("C");
        assertThat(page0.getContent())
                .extracting(Product::getPrice)
                .containsExactly(3);
        assertThat(page0.getContent())
                .extracting(Product::getImageUrl)
                .containsExactly("http://exampleC.com/img.png");
        assertThat(page0.isFirst()).isTrue();
        assertThat(page0.hasPrevious()).isFalse();
        assertThat(page0.hasNext()).isTrue();

        // when — 두 번째 페이지 조회
        Page<Product> page1 = productRepository.findAll(pageable.withPage(1));

        // then — 두 번째 페이지 콘텐츠 검증
        assertThat(page1.getContent())
                .extracting(Product::getName)
                .containsExactly("B");
        assertThat(page1.getContent())
                .extracting(Product::getPrice)
                .containsExactly(2);
        assertThat(page1.getContent())
                .extracting(Product::getImageUrl)
                .containsExactly("http://exampleB.com/img.png");
        assertThat(page1.hasPrevious()).isTrue();
        assertThat(page1.hasNext()).isTrue();

        // when — 마지막 페이지 조회
        Page<Product> page2 = productRepository.findAll(pageable.withPage(2));

        // then — 마지막 페이지 콘텐츠 검증
        assertThat(page2.getContent())
                .extracting(Product::getName)
                .containsExactly("A");
        assertThat(page2.getContent())
                .extracting(Product::getPrice)
                .containsExactly(1);
        assertThat(page2.getContent())
                .extracting(Product::getImageUrl)
                .containsExactly("http://exampleA.com/img.png");
        assertThat(page2.isLast()).isTrue();
        assertThat(page2.hasPrevious()).isTrue();
        assertThat(page2.hasNext()).isFalse();
    }
}
