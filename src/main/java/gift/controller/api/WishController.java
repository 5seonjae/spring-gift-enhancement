package gift.controller.api;

import gift.auth.LoginMember;
import gift.dto.api.WishRequestDto;
import gift.dto.api.WishResponseDto;
import gift.entity.Member;
import gift.service.WishService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public List<WishResponseDto> getWishList(@LoginMember Member member) {
        return wishService.getWishListForMember(member);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addWishItem(@LoginMember Member member,
                            @RequestBody @Valid WishRequestDto wishRequestDto) {
        wishService.addWishItemForMember(member, wishRequestDto);
    }

    @DeleteMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWishItem(@LoginMember Member member,
                               @PathVariable Long productId) {
        wishService.removeWishItemForMember(member, productId);
    }
}
