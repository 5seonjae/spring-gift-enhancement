package gift.service;

import gift.dto.api.WishRequestDto;
import gift.dto.api.WishResponseDto;
import gift.entity.Member;
import gift.entity.WishItem;
import gift.exception.InvalidMemberException;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository,
        ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public List<WishResponseDto> getWishListForMember(Member member) {
        validateMember(member);
        List<WishItem> items = wishRepository.findAllByMemberId(member.getId());
        return items.stream()
            .map(WishResponseDto::of)
            .toList();
    }

    public void addWishItemForMember(Member member, WishRequestDto wishRequestDto) {
        validateMember(member);
        if (productRepository.findById(wishRequestDto.productId()).isEmpty()) {
            throw new NoSuchElementException("상품을 찾을 수 없습니다.");
        }

        wishRepository.updateOrInsertWishItem(
            member.getId(),
            wishRequestDto.productId(),
            wishRequestDto.quantity()
        );
    }

    public void removeWishItemForMember(Member member, Long productId) {
        validateMember(member);
        if (productRepository.findById(productId).isEmpty()) {
            throw new NoSuchElementException("상품을 찾을 수 없습니다.");
        }

        int rows = wishRepository.delete(member.getId(), productId);

        if (rows == 0) {                               // ← 위시 항목 없었음
            throw new NoSuchElementException("위시 목록에 없는 상품입니다.");
        }
    }

    private void validateMember(Member member) {
        if (member == null)
            throw new InvalidMemberException("유효하지 않은 회원입니다.");
    }
}
