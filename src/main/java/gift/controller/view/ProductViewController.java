package gift.controller.view;

import static org.springframework.data.domain.Sort.Direction.DESC;

import gift.entity.Product;
import gift.service.ProductService;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductViewController {

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 목록 화면
    @GetMapping
    public String getProducts(
        Model model,
        @PageableDefault(size = 5, sort = "id", direction = DESC) Pageable pageable
    ) {
        Page<Product> page = productService.getAllProducts(pageable);
        model.addAttribute("page", page);
        return "products/user/list";
    }

    // 상품 개별 조회 요청 처리
    @GetMapping("/{id}")
    public String viewProductDetail(@PathVariable Long id,
        Model model,
        RedirectAttributes ra) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            return "products/user/detail";
        } catch (NoSuchElementException e) {
            ra.addFlashAttribute("errorMsg", "상품을 찾을 수 없습니다.");
            return "redirect:/products";
        }
    }
}

