package gift.service;

import gift.dto.api.OptionResponseDto;
import gift.repository.OptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OptionService {

    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Page<OptionResponseDto> getOptionList(Long productId, Pageable pageable) {
        return optionRepository
            .findAllByProductId(productId, pageable)
            .map(OptionResponseDto::of);
    }
}
