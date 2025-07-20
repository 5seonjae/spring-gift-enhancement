package gift;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        optionRepository.deleteAll();

        product = productRepository.save(new Product("초콜릿", 1000, "http://choco/png"));
    }

    @Test
    @DisplayName("GET /api/products/{id}/options : 옵션 페이지 반환")
    void listOptions() throws Exception {
        optionRepository.save(new Option(product, "다크 초콜릿", 10));
        optionRepository.save(new Option(product, "화이트 초콜릿", 8));

        mockMvc.perform(get("/api/products/{productId}/options", product.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].name").value("화이트 초콜릿"))
            .andExpect(jsonPath("$.content[0].quantity").value(8))
            .andExpect(jsonPath("$.content[1].name").value("다크 초콜릿"))
            .andExpect(jsonPath("$.content[1].quantity").value(10));
    }
}
