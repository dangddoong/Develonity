package com.develonity.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.develonity.board.dto.PageDto;
import com.develonity.board.dto.QuestionBoardRequest;
import com.develonity.board.dto.QuestionBoardResponse;
import com.develonity.board.dto.QuestionBoardSearchCond;
import com.develonity.board.dto.QuestionBoardUpdateRequest;
import com.develonity.board.entity.BoardImage;
import com.develonity.board.entity.QuestionBoard;
import com.develonity.board.entity.QuestionCategory;
import com.develonity.board.repository.BoardImageRepository;
import com.develonity.board.repository.BoardLikeRepository;
import com.develonity.board.repository.QuestionBoardRepository;
import com.develonity.user.entity.User;
import com.develonity.user.repository.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
//@TestInstance(Lifecycle.PER_CLASS)
class QuestionBoardServiceImplTest {

  @Autowired
  private QuestionBoardRepository questionBoardRepository;

  @Autowired
  private BoardLikeService boardLikeService;

  @Autowired
  private QuestionBoardService questionBoardService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoardImageRepository boardImageRepository;
  @Autowired
  private BoardLikeRepository boardLikeRepository;

  @BeforeEach
  void allDeleteBefore() {
    questionBoardRepository.deleteAll();
  }

  @AfterEach
  void allDeleteAfter() {
    questionBoardRepository.deleteAll();
    boardLikeRepository.deleteAll();
    boardImageRepository.deleteAll();
  }

  @Test
  @DisplayName("??????????????? ??????(?????????) & ?????? ??????")
  void createQuestionBoard() throws IOException {
    //given
    QuestionBoardRequest request = new QuestionBoardRequest("??????5", "??????5",
        100, QuestionCategory.AI);

    Optional<User> findUser = userRepository.findById(1L);
    Optional<User> readUser = userRepository.findById(2L);
    List<MultipartFile> multipartFiles = new ArrayList<>();

    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());

    multipartFiles.add(multipartFile);

    //when
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(request,
        multipartFiles, findUser.get());

    boardLikeService.addBoardLike(readUser.get().getId(), createQuestionBoard.getId());
    QuestionBoardResponse questionBoardResponse = questionBoardService.getQuestionBoard(
        createQuestionBoard.getId(), readUser.get());

    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }

    //then
    assertThat(questionBoardResponse.getTitle()).isEqualTo(createQuestionBoard.getTitle());
    assertThat(questionBoardResponse.getContent()).isEqualTo(
        createQuestionBoard.getContent());
    assertThat(questionBoardResponse.getQuestionCategory()).isEqualTo(
        createQuestionBoard.getQuestionCategory());
    assertThat(questionBoardResponse.getImagePaths()).isEqualTo(imagePaths);
    assertThat(questionBoardResponse.getNickname()).isEqualTo(findUser.get().getNickname());
    assertThat(questionBoardResponse.getPrizePoint()).isEqualTo(
        createQuestionBoard.getPrizePoint());
    assertThat(questionBoardResponse.getBoardLike()).isEqualTo(1);
    assertThat(questionBoardResponse.getHasLike()).isEqualTo(true);

//    questionBoardRepository.delete(createQuestionBoard);
  }

  @Test
  @DisplayName("??????????????? ??????(????????? ?????????) + ?????? ??????")
  void createEmptyImageQuestionBoard() throws IOException {
    //given
    QuestionBoardRequest request = new QuestionBoardRequest("??????6", "??????6",
        90, QuestionCategory.AI);

    Optional<User> findUser = userRepository.findById(1L);
    Optional<User> readUser = userRepository.findById(2L);

    List<MultipartFile> multipartFiles = new ArrayList<>();

    //when
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(request,
        multipartFiles, findUser.get());

    boardLikeService.addBoardLike(readUser.get().getId(), createQuestionBoard.getId());

    QuestionBoardResponse questionBoardResponse = questionBoardService.getQuestionBoard(
        createQuestionBoard.getId(), readUser.get());

    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }

    //then
    assertThat(questionBoardResponse.getTitle()).isEqualTo(createQuestionBoard.getTitle());
    assertThat(questionBoardResponse.getContent()).isEqualTo(
        createQuestionBoard.getContent());
    assertThat(questionBoardResponse.getQuestionCategory()).isEqualTo(
        createQuestionBoard.getQuestionCategory());
    assertThat(questionBoardResponse.getImagePaths()).isEqualTo(imagePaths);
    assertThat(questionBoardResponse.getNickname()).isEqualTo(findUser.get().getNickname());
    assertThat(questionBoardResponse.getPrizePoint()).isEqualTo(
        createQuestionBoard.getPrizePoint());
    assertThat(questionBoardResponse.getBoardLike()).isEqualTo(1);
    assertThat(questionBoardResponse.getHasLike()).isEqualTo(true);

//    questionBoardRepository.delete(createQuestionBoard);
  }

  @Test
  @DisplayName("??????????????? ??????(????????? ?????????)")
  void updateEmptyImageQuestionBoard() throws IOException {
    Optional<User> findUser = userRepository.findById(1L);

// ??????????????? ??????
    QuestionBoardRequest request = new QuestionBoardRequest("??????4", "??????4",
        5, QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    QuestionBoard createdQuestionBoard = questionBoardService.createQuestionBoard(request,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createdQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    //?????? ????????? ??????
    QuestionBoardUpdateRequest questionBoardRequest = new QuestionBoardUpdateRequest("??????4", "??????4",
        QuestionCategory.FRONTEND);

    //???????????? ????????? x??? ??????
    QuestionBoard updateQuestionBoard = questionBoardService.updateQuestionBoard(
        createdQuestionBoard.getId(), null,
        questionBoardRequest,
        findUser.get());

    //?????? ??? ????????? ?????????
    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(
        updateQuestionBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }

    assertThat(updateQuestionBoard.getTitle()).isEqualTo(questionBoardRequest.getTitle());
    assertThat(updateQuestionBoard.getContent()).isEqualTo(questionBoardRequest.getContent());
    assertThat(updateQuestionBoard.getQuestionCategory()).isEqualTo(
        questionBoardRequest.getQuestionCategory());
    assertThat(originImagePaths).isEqualTo(imagePaths);

//    questionBoardRepository.delete(updateQuestionBoard);
  }

  @Test
  @DisplayName("??????????????? ??????(????????? ??????)")
  void updateQuestionBoard() throws IOException {

    // ??????????????? ??????

    Optional<User> findUser = userRepository.findById(1L);
    QuestionBoardRequest request = new QuestionBoardRequest("??????4", "??????4",
        5, QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    QuestionBoard createdQuestionBoard = questionBoardService.createQuestionBoard(request,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createdQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    //?????? ????????? ??????(??????????????? ?????? ??????)
    QuestionBoardUpdateRequest questionBoardUpdateRequest = new QuestionBoardUpdateRequest("??????42",
        "??????42",
        QuestionCategory.FRONTEND);

    List<MultipartFile> updateMultipartFiles = new ArrayList<>();
    MockMultipartFile updateMultipartFile = new MockMultipartFile("files", "imageFile(??????).jpeg",
        "image/jpeg",
        "<<jpeg data>>".getBytes());
    updateMultipartFiles.add(updateMultipartFile);

    QuestionBoard updateQuestionBoard = questionBoardService.updateQuestionBoard(
        createdQuestionBoard.getId(), updateMultipartFiles, questionBoardUpdateRequest,
        findUser.get());

    //?????? ??? ????????? ??????
    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(
        updateQuestionBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }

    assertThat(updateQuestionBoard.getTitle()).isEqualTo(questionBoardUpdateRequest.getTitle());
    assertThat(updateQuestionBoard.getContent()).isEqualTo(questionBoardUpdateRequest.getContent());
    assertThat(updateQuestionBoard.getQuestionCategory()).isEqualTo(
        questionBoardUpdateRequest.getQuestionCategory());
    assertThat(originImagePaths).isNotEqualTo(imagePaths);

//    questionBoardRepository.delete(updateQuestionBoard);

  }

  @Test
  @DisplayName("??????????????? ??????")
  void deleteQuestionBoard() throws IOException {

    //??????????????? ??????
    Optional<User> findUser = userRepository.findById(1L);
    QuestionBoardRequest request = new QuestionBoardRequest("??????4", "??????4",
        5, QuestionCategory.BACKEND);
    QuestionBoard createdQuestionBoard = questionBoardService.createQuestionBoard(request,
        null, findUser.get());

    assertThat(questionBoardRepository.existsBoardById(createdQuestionBoard.getId())).isTrue();
    questionBoardService.deleteQuestionBoard(createdQuestionBoard.getId(), findUser.get());
    assertThat(questionBoardRepository.existsBoardById(createdQuestionBoard.getId())).isFalse();
  }


  @Test
  @DisplayName("??????????????? ???????????? + ??????")
  void getAllQuestionBoard() {

    PageDto pageDto = PageDto.builder().page(1).size(10).build();
    //????????? ??????(2???)
    QuestionBoard q = QuestionBoard.builder().questionCategory(QuestionCategory.AI)
        .userId(1L)
        .title("??????")
        .content("?????????")
        .build();

    questionBoardRepository.save(q);

    QuestionBoard q2 = QuestionBoard.builder().questionCategory(QuestionCategory.FRONTEND)
        .userId(2L)
        .title("??????")
        .content("?????????")
        .build();

    questionBoardRepository.save(q2);

    QuestionBoardSearchCond cond = QuestionBoardSearchCond.builder().build();
//    BoardSearchCond cond = BoardSearchCond.builder().build();

    //?????? ??????(2???)
    Page<QuestionBoardResponse> responsesAll = questionBoardService.searchQuestionBoardByCond(
        cond, pageDto);
    assertThat(responsesAll.getTotalElements()).isEqualTo(2);

//?????? ??????
    QuestionBoardSearchCond condTitle = QuestionBoardSearchCond.builder()
        .questionCategory(QuestionCategory.AI)
        .title("??????")
//        .content("?????????")
//        .nickname("d")
//        .boardSort(BoardSort.EMPTY)
//        .sortDirection(SortDirection.DESC)
        .build();

    Page<QuestionBoardResponse> responsesTitle = questionBoardService.searchQuestionBoardByCond(
        condTitle, pageDto);

    assertThat(responsesTitle.getTotalElements()).isEqualTo(1);

    //?????? ??????
    QuestionBoardSearchCond condContent = QuestionBoardSearchCond.builder()
        .content("?????????")
        .build();

    Page<QuestionBoardResponse> responsesContent = questionBoardService.searchQuestionBoardByCond(
        condContent, pageDto);

    assertThat(responsesContent.getTotalElements()).isEqualTo(1);

    //????????? ??????
    QuestionBoardSearchCond condNickname = QuestionBoardSearchCond.builder()
        .nickname("???")
        .build();

    Page<QuestionBoardResponse> responsesNickname = questionBoardService.searchQuestionBoardByCond(
        condNickname, pageDto);

    assertThat(responsesNickname.getTotalElements()).isEqualTo(1);

    //???????????? ??????
    QuestionBoardSearchCond condCategory = QuestionBoardSearchCond.builder()
        .questionCategory(QuestionCategory.AI).build();
    Page<QuestionBoardResponse> responsesCategory = questionBoardService.searchQuestionBoardByCond(
        condCategory, pageDto);
    assertThat(responsesCategory.getTotalElements()).isEqualTo(1);

  }
}
