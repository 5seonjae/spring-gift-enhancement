package gift.controller.api;

import static org.springframework.data.domain.Sort.Direction.DESC;

import gift.dto.api.OptionRequestDto;
import gift.dto.api.OptionResponseDto;
import gift.entity.Option;
import gift.service.OptionService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<Page<OptionResponseDto>> getAllOptions(
        @PathVariable Long productId,
        @PageableDefault(size = 5, sort = "id", direction = DESC) Pageable pageable
    ) {
        Page<OptionResponseDto> options = optionService.getOptionList(productId, pageable);
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OptionResponseDto> createOption(
        @PathVariable Long productId,
        @RequestBody @Valid OptionRequestDto optionRequestDto
    ) {
        Option saved = optionService.addOption(productId, optionRequestDto);

        URI location = URI.create(
            "/api/products/%d/options/%d".formatted(productId, saved.getId()));
        return ResponseEntity.created(location)
            .body(OptionResponseDto.of(saved));
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<OptionResponseDto> updateOption(
        @PathVariable Long productId,
        @PathVariable Long optionId,
        @RequestBody @Valid OptionRequestDto optionRequestDto
    ) {
        Option saved = optionService.updateOption(productId, optionId, optionRequestDto);

        URI location = URI.create(
            "/api/products/%d/options/%d".formatted(productId, saved.getId()));
        return ResponseEntity.created(location)
            .body(OptionResponseDto.of(saved));
    }
}
