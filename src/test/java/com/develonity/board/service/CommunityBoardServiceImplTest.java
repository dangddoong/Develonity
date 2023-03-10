package com.develonity.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.develonity.board.dto.CommunityBoardRequest;
import com.develonity.board.dto.CommunityBoardResponse;
import com.develonity.board.dto.CommunityBoardSearchCond;
import com.develonity.board.dto.PageDto;
import com.develonity.board.entity.BoardImage;
import com.develonity.board.entity.CommunityBoard;
import com.develonity.board.entity.CommunityCategory;
import com.develonity.board.repository.BoardImageRepository;
import com.develonity.board.repository.BoardLikeRepository;
import com.develonity.board.repository.CommunityBoardRepository;
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
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestInstance(Lifecycle.PER_CLASS)
class CommunityBoardServiceImplTest {

  @Autowired
  private CommunityBoardRepository communityBoardRepository;

  @Autowired
  private BoardLikeService boardLikeService;

  @Autowired
  private CommunityBoardService communityBoardService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoardImageRepository boardImageRepository;

  @Autowired
  private BoardLikeRepository boardLikeRepository;

  @BeforeEach
  void allDeleteBefore() {
    communityBoardRepository.deleteAll();
  }

  @AfterEach
  void allDeleteAfter() {
    communityBoardRepository.deleteAll();
    boardLikeRepository.deleteAll();
    boardImageRepository.deleteAll();
  }

  @Test
  @DisplayName("??????????????? ??????(?????????) & ?????? ??????")
  void createCommunityBoard() throws IOException {

    //given
    CommunityBoardRequest request = new CommunityBoardRequest("??????2", "??????2",
        CommunityCategory.NORMAL);

    Optional<User> findUser = userRepository.findById(1L);
    Optional<User> readUser = userRepository.findById(2L);
    List<MultipartFile> multipartFiles = new ArrayList<>();

    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());

    multipartFiles.add(multipartFile);

    //when
    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(request,
        multipartFiles, findUser.get());

    boardLikeService.addBoardLike(readUser.get().getId(), createCommunityBoard.getId());

    CommunityBoardResponse communityBoardResponse = communityBoardService.getCommunityBoard(
        createCommunityBoard.getId(), readUser.get());

    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }
    //then

    assertThat(communityBoardResponse.getTitle()).isEqualTo(createCommunityBoard.getTitle());
    assertThat(communityBoardResponse.getContent()).isEqualTo(
        createCommunityBoard.getContent());
    assertThat(communityBoardResponse.getCommunityCategory()).isEqualTo(
        createCommunityBoard.getCommunityCategory());
    assertThat(communityBoardResponse.getImagePaths()).isEqualTo(imagePaths);
    assertThat(communityBoardResponse.getNickname()).isEqualTo(findUser.get().getNickname());
    assertThat(communityBoardResponse.getBoardLike()).isEqualTo(1);
    assertThat(communityBoardResponse.isHasLike()).isEqualTo(true);

//    communityBoardRepository.delete(createCommunityBoard);
  }

  @Test
  @DisplayName("??????????????? ??????(????????? ?????????) & ?????? ??????")
  void createEmptyImageCommunityBoard() throws IOException {

    //given
    CommunityBoardRequest request = new CommunityBoardRequest("????????????", "????????????",
        CommunityCategory.NORMAL);

    Optional<User> findUser = userRepository.findById(1L);
    Optional<User> readUser = userRepository.findById(2L);

    List<MultipartFile> multipartFiles = new ArrayList<>();

    //when
    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(request,
        multipartFiles, findUser.get());

    boardLikeService.addBoardLike(readUser.get().getId(), createCommunityBoard.getId());

    CommunityBoardResponse communityBoardResponse = communityBoardService.getCommunityBoard(
        createCommunityBoard.getId(), readUser.get());

    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }

    //then
    assertThat(communityBoardResponse.getTitle()).isEqualTo(createCommunityBoard.getTitle());
    assertThat(communityBoardResponse.getContent()).isEqualTo(
        createCommunityBoard.getContent());
    assertThat(communityBoardResponse.getCommunityCategory()).isEqualTo(
        createCommunityBoard.getCommunityCategory());
    assertThat(communityBoardResponse.getImagePaths()).isEqualTo(imagePaths);
    assertThat(communityBoardResponse.getNickname()).isEqualTo(findUser.get().getNickname());
    assertThat(communityBoardResponse.getBoardLike()).isEqualTo(1);
    assertThat(communityBoardResponse.isHasLike()).isEqualTo(true);

//    communityBoardRepository.delete(createCommunityBoard);
  }

  @Test
  @DisplayName("??????????????? ??????(????????? ?????????)")
  void updateEmptyImageCommunityBoard() throws IOException {

    Optional<User> findUser = userRepository.findById(1L);

    //????????? ??????
    CommunityBoardRequest request = new CommunityBoardRequest("??????3", "??????3",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(request,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }
    //????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????1", "??????1",
        CommunityCategory.GRADE);
    //???????????? ????????? x??? ??????
    CommunityBoard updateCommunityBoard = communityBoardService.updateCommunityBoard(
        createCommunityBoard.getId(),
        null, communityBoardRequest,
        findUser.get());

    //?????? ??? ????????? ?????????
    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(
        updateCommunityBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }

    assertThat(updateCommunityBoard.getTitle()).isEqualTo(communityBoardRequest.getTitle());
    assertThat(updateCommunityBoard.getContent()).isEqualTo(communityBoardRequest.getContent());
    assertThat(updateCommunityBoard.getCommunityCategory()).isEqualTo(
        communityBoardRequest.getCommunityCategory());
    assertThat(originImagePaths).isEqualTo(imagePaths);

//    communityBoardRepository.delete(updateCommunityBoard);
  }

  @Test
  @DisplayName("??????????????? ??????(????????? ??????)")
  void updateCommunityBoard() throws IOException {
//????????? ??????
    Optional<User> findUser = userRepository.findById(1L);
    CommunityBoardRequest request = new CommunityBoardRequest("??????3", "??????3",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(request,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }
//????????? ??????(????????? ?????? ?????? ??????)

    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????12", "??????12",
        CommunityCategory.GRADE);

    List<MultipartFile> updateMultipartFiles = new ArrayList<>();

    MockMultipartFile updateMultipartFile = new MockMultipartFile("files", "imageFile(??????).jpeg",
        "image/jpeg",
        "<<jpeg data>>".getBytes());
    updateMultipartFiles.add(updateMultipartFile);

    CommunityBoard updateCommunityBoard = communityBoardService.updateCommunityBoard(
        createCommunityBoard.getId(), updateMultipartFiles, communityBoardRequest,
        findUser.get());
//?????? ??? ????????? ??????
    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(1L);
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }

    assertThat(updateCommunityBoard.getTitle()).isEqualTo(communityBoardRequest.getTitle());
    assertThat(updateCommunityBoard.getContent()).isEqualTo(communityBoardRequest.getContent());
    assertThat(updateCommunityBoard.getCommunityCategory()).isEqualTo(
        communityBoardRequest.getCommunityCategory());
    assertThat(originImagePaths).isNotEqualTo(imagePaths);

//    communityBoardRepository.delete(updateCommunityBoard);
  }

  @Test
  @DisplayName("????????? ??????")
  void deleteCommunityBoard() throws IOException {
    Optional<User> findUser = userRepository.findById(1L);
    //??????????????? ??????
    CommunityBoardRequest request = new CommunityBoardRequest("??????3", "??????3",
        CommunityCategory.NORMAL);
    CommunityBoard communityBoard = communityBoardService.createCommunityBoard(
        request,
        null, findUser.get());

//    System.out.println(communityBoardRepository.findById(1L));
    assertThat(communityBoardRepository.existsBoardById(communityBoard.getId())).isTrue();
    communityBoardService.deleteCommunityBoard(communityBoard.getId(), findUser.get());
    assertThat(communityBoardRepository.existsBoardById(communityBoard.getId())).isFalse();
  }

  @Test
  @DisplayName("????????? ???????????? + ??????")
  void getAllCommunityBoard() {

    PageDto pageDto = PageDto.builder().page(1).size(10).build();
    //???????????? ?????? ??????(2???)
    CommunityBoard c = CommunityBoard.builder().communityCategory(CommunityCategory.GRADE)
        .userId(1L) //??????????????? ?????? 1,2???
        .title("??????")
        .content("?????????")
        .build();

    communityBoardRepository.save(c);

    CommunityBoard c2 = CommunityBoard.builder().communityCategory(CommunityCategory.NORMAL)
        .userId(2L)
        .title("??????")
        .content("?????????")
        .build();

    communityBoardRepository.save(c2);
    CommunityBoardSearchCond cond = CommunityBoardSearchCond.builder().build();

    //?????? ??????(2???, ?????????????????? BeforeEach??? ??????)
    Page<CommunityBoardResponse> responsesAll = communityBoardService.searchCommunityBoardByCond(
        cond, pageDto);
    assertThat(responsesAll.getTotalElements()).isEqualTo(2);

//?????? ??????
    CommunityBoardSearchCond condTitle = CommunityBoardSearchCond.builder()
        .communityCategory(CommunityCategory.GRADE)
        .title("??????")
//        .content("?????????")
//        .nickname("d")
//        .boardSort(BoardSort.EMPTY)
//        .sortDirection(SortDirection.DESC)
        .build();

    Page<CommunityBoardResponse> responsesTitle = communityBoardService.searchCommunityBoardByCond(
        condTitle, pageDto);

    assertThat(responsesTitle.getTotalElements()).isEqualTo(1);

    //?????? ??????
    CommunityBoardSearchCond condContent = CommunityBoardSearchCond.builder()
        .content("?????????")
        .build();

    Page<CommunityBoardResponse> responsesContent = communityBoardService.searchCommunityBoardByCond(
        condContent, pageDto);

    assertThat(responsesContent.getTotalElements()).isEqualTo(1);

    //????????? ??????(??????????????? ???????????? ?????? ????????? ?????? 3???)
    CommunityBoardSearchCond condNickname = CommunityBoardSearchCond.builder()
        .nickname("???")
        .build();

    Page<CommunityBoardResponse> responsesNickname = communityBoardService.searchCommunityBoardByCond(
        condNickname, pageDto);

    assertThat(responsesNickname.getTotalElements()).isEqualTo(1);

    //???????????? ??????
    CommunityBoardSearchCond condCategory = CommunityBoardSearchCond.builder()
        .communityCategory(CommunityCategory.NORMAL).build();
    Page<CommunityBoardResponse> responsesCategory = communityBoardService.searchCommunityBoardByCond(
        condCategory, pageDto);
    assertThat(responsesCategory.getTotalElements()).isEqualTo(1);
  }
}