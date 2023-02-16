package com.develonity.order.controller;

import com.develonity.order.dto.GiftCardRegister;
import com.develonity.order.dto.GiftCardResponse;
import com.develonity.order.dto.PageDTO;
import com.develonity.order.entity.GiftCardCategory;
import com.develonity.order.service.GiftCardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
//@PreAuthorize("hasAnyRole('ROLE_ADMIN')") //어드민만 접근 가능, 기프트카드 조회, 단건 조회는 일반유저도 접근 가능
public class GiftCardController {
    private final GiftCardServiceImpl giftCardService;

    //기프트 카드 등록
    @PostMapping("/gift-cards")
    public Long registerGiftCard(@RequestBody GiftCardRegister giftCardRegister) {
        return giftCardService.registerGiftCard(giftCardRegister);
    }

    //기프트 카드 전체 조회
    @GetMapping("/gift-cards")
    public List<GiftCardResponse> getGiftCardList() {
        return giftCardService.getGiftCardList();
    }

    //카테고리 별 기프트 카드 조회 (Enum 의 Long 값을 넣어줄 때)
    @GetMapping("/gift-cards/categories/{categoryId}")
    public List<GiftCardResponse> getCategorizedGiftCardList(@PathVariable Long categoryId) {
        return giftCardService.getCategorizedGiftCardList(categoryId);
    }

    //기프트 카드 전체 조회(페이징)
    @GetMapping("/gift-cards/paging") //테스트를 위한 API, 추후 수정 예정
    public Page<GiftCardResponse> getGiftCardListByPaging(@RequestBody PageDTO pageDTO) {
        return giftCardService.getGiftCardListByPaging(pageDTO);
    }

    //기프트 카드 단건 조회
    @GetMapping("/gift-cards/{giftCardId}")
    public GiftCardResponse getGiftCard(@PathVariable Long giftCardId) {
        return giftCardService.getGiftCard(giftCardId);
    }

    //기프트 카드 수정
    @PutMapping("/gift-cards/{giftCardId}")
    public Long updateGiftCard(@PathVariable Long giftCardId, @RequestBody GiftCardRegister giftCardRegister) {
        return giftCardService.updateGiftCard(giftCardId, giftCardRegister);
    }

    //기프트 카드 삭제
    @DeleteMapping("/gift-cards/{giftCardId}")
    public Long deleteGiftCard(@PathVariable Long giftCardId) {
        return giftCardService.deleteGiftCard(giftCardId);
    }
}
