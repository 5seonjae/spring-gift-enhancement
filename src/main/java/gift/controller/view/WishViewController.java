package gift.controller.view;

import gift.auth.LoginMember;
import gift.dto.api.WishRequestDto;
import gift.dto.api.WishResponseDto;
import gift.entity.Member;
import gift.service.WishService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/wishes")
public class WishViewController {

    private final WishService wishService;

    public WishViewController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public String getWishList(@LoginMember Member member, Model model) {
        List<WishResponseDto> items = wishService.getWishListForMember(member);
        model.addAttribute("items", items);
        return "wishes/list";           // → templates/wishes/list.html
    }

    @PostMapping("/{productId}")                 // ★ 추가
    public String add(@PathVariable Long productId,
                      @LoginMember Member member,
                      RedirectAttributes ra) {
        wishService.addWishItemForMember(member,
                new WishRequestDto(productId, /*quantity*/ 1));
        ra.addFlashAttribute("msg", "위시 리스트에 담겼습니다!");
        return "redirect:/products";            // 상품 목록으로 되돌리거나 /wishes 로
    }

    @PostMapping("/{productId}/delete")
    public String delete(@PathVariable Long productId,
                         @LoginMember Member member,
                         RedirectAttributes ra) {
        wishService.removeWishItemForMember(member, productId);
        ra.addFlashAttribute("msg", "삭제 완료!");
        return "redirect:/wishes";
    }
}
