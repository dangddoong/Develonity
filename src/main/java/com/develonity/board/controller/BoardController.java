package com.develonity.board.controller;

import com.develonity.board.dto.BoardPage;
import com.develonity.board.dto.BoardResponse;
import com.develonity.board.dto.CommunityBoardRequest;
import com.develonity.board.dto.CommunityBoardResponse;
import com.develonity.board.dto.CommunityBoardSearchCond;
import com.develonity.board.dto.PageDto;
import com.develonity.board.dto.QuestionBoardRequest;
import com.develonity.board.dto.QuestionBoardResponse;
import com.develonity.board.dto.QuestionBoardSearchCond;
import com.develonity.board.dto.QuestionBoardUpdateRequest;
import com.develonity.board.entity.CommunityBoard;
import com.develonity.board.repository.CommunityBoardRepositoryImpl;
import com.develonity.board.service.BoardLikeService;
import com.develonity.board.service.BoardService;
import com.develonity.board.service.CommunityBoardService;
import com.develonity.board.service.QuestionBoardService;
import com.develonity.board.service.ScrapService;
import com.develonity.common.aws.service.AwsPreSignedUrlService;
import com.develonity.common.exception.CustomException;
import com.develonity.common.exception.ExceptionStatus;
import com.develonity.common.security.users.UserDetailsImpl;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {

  private final QuestionBoardService questionBoardService;

  private final CommunityBoardService communityBoardService;

  private final BoardService boardService;
  private final BoardLikeService boardLikeService;

  private final CommunityBoardRepositoryImpl communityBoardRepository;
  private final ScrapService scrapService;

  private final AwsPreSignedUrlService awsPreSignedUrlService;


  //QueryDsl ????????? ????????????
  @GetMapping("/community-boards")
  public Page<CommunityBoardResponse> getCommunityBoardsPage(
      CommunityBoardSearchCond cond, PageDto pageDto
  ) {
    return communityBoardService.searchCommunityBoardByCond(cond, pageDto);
  }


  //  ??????????????? ??????
  @PostMapping("/question-boards")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> createQuestionBoard(
      @RequestPart(required = false, name = "images") List<MultipartFile> multipartFiles,
      @RequestPart("request") @Valid QuestionBoardRequest request,
      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

    if (request.getQuestionCategory() == null) {
      throw new CustomException(ExceptionStatus.COMMENT_IS_NOT_EXIST);
    }

    questionBoardService.createQuestionBoard(request, multipartFiles, userDetails.getUser());
    return new ResponseEntity<>("?????? ???????????? ?????????????????????", HttpStatus.CREATED);
  }

  // ?????? ????????? ??????
  @PostMapping("/community-boards")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> createCommunityBoard(
      @RequestPart(required = false, name = "images") List<MultipartFile> multipartFiles,
      @RequestPart("request") CommunityBoardRequest request,
      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

    if (request.getCommunityCategory() == null) {
      throw new CustomException(ExceptionStatus.CATEGORY_IS_NOT_EXIST);
    }
    communityBoardService.createCommunityBoard(request, multipartFiles, userDetails.getUser());
    return new ResponseEntity<>("?????? ???????????? ?????????????????????", HttpStatus.CREATED);
  }

  //??????????????? ??????
  @PutMapping("/question-boards/{boardId}")
  public ResponseEntity<String> updateQuestionBoard(@PathVariable Long boardId,
      @RequestPart(required = false, name = "images") List<MultipartFile> multipartFiles,
      @RequestPart("request") QuestionBoardUpdateRequest request,
      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
    questionBoardService.updateQuestionBoard(boardId, multipartFiles, request,
        userDetails.getUser());
    return new ResponseEntity<>("?????? ???????????? ?????????????????????.", HttpStatus.OK);
  }

  //?????? ????????? ?????? (+?????????)
  @PutMapping("/community-boards/{boardId}")
  public ResponseEntity<String> updateCommunityBoard(@PathVariable Long boardId,
      @RequestPart(required = false, name = "images") List<MultipartFile> multipartFiles,
      @RequestPart("request") CommunityBoardRequest request,
      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
    communityBoardService.updateCommunityBoard(boardId, multipartFiles, request,
        userDetails.getUser());
    return new ResponseEntity<>("?????? ???????????? ?????????????????????.", HttpStatus.OK);
  }

  //??????????????? ??????
  @DeleteMapping("/question-boards/{boardId}")
  public ResponseEntity<String> deleteQuestionBoard(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    questionBoardService.deleteQuestionBoard(boardId, userDetails.getUser());
    return new ResponseEntity<>("?????? ???????????? ?????????????????????.", HttpStatus.OK);
  }

  //??????????????? ??????
  @DeleteMapping("/community-boards/{boardId}")
  public ResponseEntity<String> deleteCommunityBoard(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    communityBoardService.deleteCommunityBoard(boardId, userDetails.getUser());
    return new ResponseEntity<>("?????? ???????????? ?????????????????????.", HttpStatus.OK);
  }

  //QueryDsl ????????? ????????????
  @GetMapping("/question-boards")
  public Page<QuestionBoardResponse> getQuestionBoardsPage(
      QuestionBoardSearchCond questionBoardSearchCond,
      PageDto pageDto
  ) {
    return questionBoardService.searchQuestionBoardByCond(questionBoardSearchCond, pageDto);
  }

  //  ????????? ???????????? 3???(?????? ????????? ??????, ??????????????? ?????? ????????? ?????? ??????)
  @GetMapping("/question-boards/todayBest")
  public List<QuestionBoardResponse> getQuestionBoardOrderByLikes(
      QuestionBoardSearchCond cond
  ) {
    return questionBoardService.questionBoardOrderBy(cond);
  }

  //??????????????? ?????? ??????
  @GetMapping("/question-boards/{boardId}")
  public QuestionBoardResponse getQuestionBoard(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return questionBoardService.getQuestionBoard(boardId, userDetails.getUser());
  }

  //??????????????? ?????? ??????
  @GetMapping("community-boards/{boardId}")
  public CommunityBoardResponse getCommunityBoard(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return communityBoardService.getCommunityBoard(boardId, userDetails.getUser());
  }


  //?????? ????????? ?????????
  @PostMapping("/question-boards/{boardId}/likes")
  public ResponseEntity<String> addQuestionBoardLike(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    if (!questionBoardService.isExistBoard(boardId)) {
      throw new CustomException(ExceptionStatus.BOARD_IS_NOT_EXIST);
    }
    boardLikeService.addBoardLike(userDetails.getUser().getId(), boardId);
    return new ResponseEntity<>("????????? ??????!", HttpStatus.CREATED);
  }


  //?????? ????????? ????????? ??????
  @DeleteMapping("/question-boards/{boardId}/likes")
  public ResponseEntity<String> cancelQuestionBoardLike(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    boardLikeService.cancelBoardLike(userDetails.getUser().getId(), boardId);
    return new ResponseEntity<>("????????? ??????!", HttpStatus.OK);
  }

  //?????? ????????? ?????????
  @PostMapping("/community-boards/{boardId}/likes")
  public ResponseEntity<String> addCommunityBoardLike(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    if (!communityBoardService.ExistsBoard(boardId)) {
      throw new CustomException(ExceptionStatus.BOARD_IS_NOT_EXIST);
    }
    boardLikeService.addBoardLike(userDetails.getUser().getId(), boardId);
    return new ResponseEntity<>("????????? ??????!", HttpStatus.CREATED);
  }

  //?????? ????????? ????????? ??????
  @DeleteMapping("/community-boards/{boardId}/likes")
  public ResponseEntity<String> cancelCommunityBoardLike(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    boardLikeService.cancelBoardLike(userDetails.getUser().getId(), boardId);
    return new ResponseEntity<>("????????? ??????!", HttpStatus.OK);
  }

  //?????? ??????
  @PostMapping("/community-boards/{boardId}/grade")
  public ResponseEntity<String> changeGrade(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    if (!communityBoardService.ExistsBoard(boardId)) {
      throw new CustomException(ExceptionStatus.BOARD_IS_NOT_EXIST);
    }
    if (!communityBoardService.isGradeBoard(boardId)) {
      throw new CustomException(ExceptionStatus.CATEGORY_DO_NOT_MATCH);
    }
    CommunityBoard communityBoard = communityBoardService.getCommunityBoardAndCheck(boardId);
    communityBoardService.upgradeGrade(communityBoard.getUserId(), boardId);
    return new ResponseEntity<>("?????? ?????? ??????", HttpStatus.OK);
  }

  //????????? ??????
  @PostMapping("/boards/{boardId}/scrap")
  public ResponseEntity<String> addScrap(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    scrapService.addScrap(userDetails.getUser().getId(), boardId);
    return new ResponseEntity<>("????????? ??????!", HttpStatus.CREATED);
  }

  //????????? ??????
  @DeleteMapping("/boards/{boardId}/scrap")
  public ResponseEntity<String> cancelScrap(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    scrapService.cancelScrap(userDetails.getUser().getId(), boardId);
    return new ResponseEntity<>("????????? ??????!", HttpStatus.OK);
  }

  //?????? ????????? ????????? ????????? ?????? ??????
  @GetMapping("/user/me/scraps")
  public Page<BoardResponse> getScrapsPage(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      BoardPage boardPage
  ) {
    return boardService.getScrapBoardPage(userDetails.getUser(), boardPage);
  }

  //?????? ??? ????????? ?????? (??????,???????????? ??????)
  @GetMapping("/user/me/communityBoards")
  public Page<CommunityBoardResponse> getMyCommunityBoardPage(
      CommunityBoardSearchCond cond, PageDto pageDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    return communityBoardService.searchMyCommunityBoardByCond(cond, pageDto,
        userDetails.getUser().getId());
  }

  //?????? ??? ????????? ?????? (??????,???????????? ??????)
  @GetMapping("/user/me/questionBoards")
  public Page<QuestionBoardResponse> getMyQuestionBoardPage(
      QuestionBoardSearchCond cond, PageDto pageDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    return questionBoardService.searchMyQuestionBoardByCond(cond, pageDto,
        userDetails.getUser().getId());
  }

//  //??????????????? ?????? ??????(querydsl ????????????)
//  @GetMapping("/question-boards/before")
//  public Page<QuestionBoardResponse> getQuestionBoardsPage(
//      @AuthenticationPrincipal UserDetailsImpl userDetails,
//      BoardPage questionBoardPage
//  ) {
//    return questionBoardService.getQuestionBoardPage(userDetails.getUser(), questionBoardPage);
//  }
//
//
//  //??????????????? ?????? ??????(querydsl ????????????)
//  @GetMapping("/community-boards/before")
//  public Page<CommunityBoardResponse> getCommunityBoardsPage(
//      @AuthenticationPrincipal UserDetailsImpl userDetails,
//      BoardPage communityBoardPage
//  ) {
//    return communityBoardService.getCommunityBoardPage(userDetails.getUser(), communityBoardPage);
//  }
//
//
//  //??????????????? ?????? ??????(????????????)
//  @GetMapping("/question-boards/test")
//  public Page<QuestionBoardResponse> getTestQuestionBoardsPage(
//      @AuthenticationPrincipal UserDetailsImpl userDetails,
//      BoardPage questionBoardPage
//  ) {
//    return questionBoardService.getTestQuestionBoardPage(userDetails.getUser(), questionBoardPage);
//  }
//
//  //??????????????? ?????? ??????(????????????)
//  @GetMapping("/community-boards/test")
//  public Page<CommunityBoardResponse> getTestCommunityBoardsPage(
//      @AuthenticationPrincipal UserDetailsImpl userDetails,
//      BoardPage communityBoardPage
//  ) {
//    return communityBoardService.getTestCommunityBoardPage(userDetails.getUser(),
//        communityBoardPage);
//  }

}
