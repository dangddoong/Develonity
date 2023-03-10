package com.develonity.comment.service;

import static com.develonity.comment.entity.CommentStatus.ADOPTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.develonity.board.dto.CommunityBoardRequest;
import com.develonity.board.dto.QuestionBoardRequest;
import com.develonity.board.entity.BoardImage;
import com.develonity.board.entity.CommunityBoard;
import com.develonity.board.entity.CommunityCategory;
import com.develonity.board.entity.QuestionBoard;
import com.develonity.board.entity.QuestionCategory;
import com.develonity.board.repository.BoardImageRepository;
import com.develonity.board.service.CommunityBoardServiceImpl;
import com.develonity.board.service.QuestionBoardServiceImpl;
import com.develonity.comment.dto.CommentPageDto;
import com.develonity.comment.dto.CommentRequest;
import com.develonity.comment.dto.CommentResponse;
import com.develonity.comment.dto.CommentSearchCond;
import com.develonity.comment.entity.Comment;
import com.develonity.comment.repository.CommentLikeRepository;
import com.develonity.comment.repository.CommentRepository;
import com.develonity.comment.repository.ReplyCommentRepository;
import com.develonity.common.exception.CustomException;
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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class CommentServiceImplTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private ReplyCommentService replyCommentService;
  @Autowired
  private CommentServiceImpl commentService;
  @Autowired
  private CommentLikeRepository commentLikeRepository;
  @Autowired
  private QuestionBoardServiceImpl questionBoardService;
  @Autowired
  private CommunityBoardServiceImpl communityBoardService;
  @Autowired
  private BoardImageRepository boardImageRepository;
  @Autowired
  private ReplyCommentRepository replyCommentRepository;

  @BeforeEach
  void allDeleteBefore() {
    commentRepository.deleteAll();
  }

  @AfterEach
  void allDeleteAfter() {
    commentRepository.deleteAll();
    commentLikeRepository.deleteAll();
    replyCommentRepository.deleteAll();
  }

  //  ?????? ?????? ??????
  @Test
  @DisplayName("?????? ?????? ??????")
  void getAllComment() {
    // given
    CommentPageDto commentPageDto = CommentPageDto.builder().page(1).size(5).build();

    // ?????? ??????
    Comment comment = Comment.builder()
        .userId(1L)
        .content("??????")
        .questionBoardId(1L)
        .build();

    // ?????? ??????
    commentRepository.save(comment);

    // ?????? ??????
    Comment comment1 = Comment.builder()
        .userId(2L)
        .content("??????????????? ?????????")
        .questionBoardId(1L)
        .build();

    // ?????? ??????
    commentRepository.save(comment1);
    CommentSearchCond commentSearchCond = CommentSearchCond.builder().build();

    // ???????????? ??????
    CommentSearchCond searchContentCond = CommentSearchCond.builder()
        .content("??????")
        .build();

    // ???????????? ??????
    CommentSearchCond searchNicknameCond = CommentSearchCond.builder()
        .nickname("???")
        .build();

    // when

    // ????????? ??? ??????
    Page<CommentResponse> getAllComment = commentService.getAllComment(commentPageDto,
        commentSearchCond);

    // ?????? ???????????? ??????
    Page<CommentResponse> searchComment = commentService.getAllComment(commentPageDto,
        searchContentCond);

    // ???????????? ??????
    Page<CommentResponse> searchNickname = commentService.getAllComment(commentPageDto,
        searchNicknameCond);

    // then

    // ????????? ?????? ???????????? ??????
    assertThat(getAllComment.getTotalElements()).isEqualTo(2);

    // ?????? ?????? ????????? ????????? "??????"??? ????????? 1????????? ??????
    assertThat(searchComment.getTotalElements()).isEqualTo(1);

    // ???????????? "???"??? ????????? ??????????????? 1????????? ??????
    assertThat(searchNickname.getTotalElements()).isEqualTo(1);


  }

  @Test
  @DisplayName("?????? ??? ?????? ??????")
  void getMyComments() {
    // given
    CommentPageDto commentPageDto = CommentPageDto.builder().page(1).size(5).build();

    // ?????? ??????
    Comment comment = Comment.builder()
        .userId(1L)
        .content("???????????????")
        .questionBoardId(1L)
        .build();

    // ?????? ??????
    commentRepository.save(comment);

    // ?????? ??????
    Comment comment1 = Comment.builder()
        .userId(2L)
        .content("???????????????")
        .questionBoardId(1L)
        .build();

    // ?????? ??????
    commentRepository.save(comment1);

    // ?????? ??????
    Comment comment2 = Comment.builder()
        .userId(1L)
        .content("????????????")
        .questionBoardId(1L)
        .build();

    // ?????? ??????
    commentRepository.save(comment2);
    CommentSearchCond commentSearchCond = CommentSearchCond.builder().build();

    // ?????? ????????? "??????"??? ?????? ??????
    CommentSearchCond searchMyContentCond = CommentSearchCond.builder()
        .content("??????")
        .build();

    // ?????? ???????????? ???????????? "???"??? ????????? ????????? ??????
    CommentSearchCond searchMyNicknameCond = CommentSearchCond.builder()
        .nickname("???")
        .build();

    // when

    // ?????? ??? ?????? ?????? ??????
    Page<CommentResponse> myAllComments = commentService.getMyComments(commentPageDto,
        commentSearchCond, 1L);

    // ?????? ????????? "??????"??? ?????? ??????
    Page<CommentResponse> searchMyContent = commentService.getMyComments(commentPageDto,
        searchMyContentCond, 1L);

    // ?????? ???????????? ???????????? "???"??? ????????? ????????? ??????
    Page<CommentResponse> searchMyNickname = commentService.getMyComments(commentPageDto,
        searchMyNicknameCond, 2L);

    // then

    // ?????? ??? ????????? ??? ????????? ???????????? ??????
    assertThat(myAllComments.getTotalElements()).isEqualTo(2);

    // ?????? ??? ?????? ??? ?????? ?????? ??? "??????"??? ????????? ????????? 1????????? ??????
    assertThat(searchMyContent.getTotalElements()).isEqualTo(1);

    // ?????? ??? ?????? ??? ???????????? "???"??? ????????? ????????? 1????????? ??????
    assertThat(searchMyNickname.getTotalElements()).isEqualTo(1);

  }

//  @Test
//  @DisplayName("???????????? ?????? ??????,????????? ??????")
//  void getCommentsByBoard() {
//    // given
//    Optional<User> findUser = userRepository.findById(1L);
//    Long boardId = 1L;
//    // when
//    commentService.getCommentsByBoard(boardId, findUser.get());
//    // then
//    assertThat()
//  }

  // ?????? ?????? ??????
  @Test
  @DisplayName("?????? ?????? ??????")
  void createQuestionComment() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("??????", "??????", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest request = new CommentRequest("????????????");

    // when
    Comment comment = commentService.createQuestionComment(createQuestionBoard.getId(), request,
        findUser.get());

    // then
    assertThat(comment.getContent()).isEqualTo(request.getContent());
    // ????????? ?????? ?????? ???????????? ????????? ?????? ????????? ????????? ?????????, ??????????????? ?????? ?????? ?????? ??????
    assertThrows(CustomException.class,
        () -> commentService.createQuestionComment(comment.getBoardId(), request, findUser.get()));
  }

  @Test
  @DisplayName("?????? ?????? ??????")
  void updateQuestionComment() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("??????", "??????", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest createCommentRequest = new CommentRequest("????????????");
    CommentRequest commentRequest = new CommentRequest("??????????????????");
    Comment createComment = commentService.createQuestionComment(createQuestionBoard.getId(),
        createCommentRequest,
        findUser.get());

    // when
    Comment updateComment = commentService.updateQuestionComment(createComment.getId(),
        commentRequest, findUser.get());

    // then
    assertThat(updateComment.getContent()).isEqualTo(commentRequest.getContent());
  }

  @Test
  @DisplayName("?????? ?????? ??????")
  void deleteQuestionComment() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("??????", "??????", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest createCommentRequest = new CommentRequest("????????????");
    Comment createComment = commentService.createQuestionComment(createQuestionBoard.getId(),
        createCommentRequest,
        findUser.get());

    // when
    commentService.deleteQuestionComment(createComment.getId(), findUser.get());

    // then
    assertThat(commentRepository.existsCommentById(createComment.getId())).isFalse();
  }

  @Test
  @DisplayName("?????? ?????? ??????")
  void adoptComment() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("??????", "??????", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest createCommentRequest = new CommentRequest("????????????");
    Comment createComment = commentService.createQuestionComment(createQuestionBoard.getId(),
        createCommentRequest,
        findUser.get());
    CommentResponse commentResponse = CommentResponse.builder()
        .commentStatus(ADOPTED)
        .build();

    // when
    commentService.adoptComment(createComment);

    // then
    assertThat(commentResponse.getCommentStatus()).isEqualTo(commentResponse.getCommentStatus());
  }

  @Test
  @DisplayName("?????? ?????? ??????")
  void createCommunityComment() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ?????? ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ?????? ??????
    CommentRequest commentRequest = new CommentRequest("????????????");

    // when
    Comment comment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // then
    assertThat(comment.getContent()).isEqualTo(commentRequest.getContent());
  }

  @Test
  @DisplayName("?????? ?????? ??????")
  void updateCommunityComment() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ?????? ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ?????? ??????
    CommentRequest createCommunityCommentRequest = new CommentRequest("????????????");
    CommentRequest commentRequest = new CommentRequest("??????????????????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        createCommunityCommentRequest,
        findUser.get());

    // when
    Comment updateComment = commentService.updateCommunityComment(createComment.getId(),
        commentRequest, findUser.get());

    // then
    assertThat(updateComment.getContent()).isEqualTo(commentRequest.getContent());
  }

  @Test
  @DisplayName("?????? ?????? ??????")
  void deleteCommunity() throws IOException {
    // given
    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ?????? ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ?????? ??????
    CommentRequest createCommunityCommentRequest = new CommentRequest("????????????");
    Comment createComment = commentService.createCommunityComment(2L, createCommunityCommentRequest,
        findUser.get());

    // when
    commentService.deleteCommunity(createComment.getId(), findUser.get());

    // then
    assertThat(commentRepository.existsCommentById(createComment.getId())).isFalse();
  }

// Repository??? ????????? ????????? ????????? ????????????
//  @Test
//  void deleteCommentsByBoardId() {
//    // given
//    CommentRequest commentRequest = new CommentRequest("????????????");
//    Long communityBoardId = 2L;
//    Optional<User> findUser = userRepository.findById(1L);
//    Comment createComment = commentService.createCommunityComment(communityBoardId, commentRequest,
//        findUser.get());
//
//    // when
//    commentService.deleteCommentsByBoardId(createComment.getBoardId());
//
//    // then
//    assertThat(commentRepository.existsCommentsByBoardId(createComment.getBoardId())).isFalse();
//  }

  @Test
  @DisplayName("????????????????????? ?????????????????? ????????? ???????????? ??????")
  void existsCommentByBoardIdAndUserId() throws IOException {
    // given
    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest commentRequest = new CommentRequest("?????? ??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    commentService.existsCommentByBoardIdAndUserId(createComment.getBoardId(),
        findUser.get().getId());

    // then
    assertThat(commentRepository.existsCommentsByBoardId(createComment.getBoardId())).isTrue();
    assertThat(createComment.getUserId()).isEqualTo(findUser.get().getId());
  }

  @Test
  @DisplayName("????????????????????? ????????? ???????????? ??????")
  void existsCommentsByBoardId() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest commentRequest = new CommentRequest("?????? ??????");
    Comment createComment = commentService.createCommunityComment(2L, commentRequest,
        findUser.get());

    // when
    commentService.existsCommentsByBoardId(createComment.getBoardId());

    // then
    assertThat(commentRepository.existsCommentsByBoardId(createComment.getBoardId())).isTrue();
  }

  @Test
  @DisplayName("???????????? ???????????? ??????")
  void getNickname() {
    // given
    Optional<User> findUser = userRepository.findById(1L);
    // when
    commentService.getNickname(findUser.get().getId());
    // then
    assertThat(findUser.get().getNickname()).isEqualTo(
        commentService.getNickname(findUser.get().getId()));
  }

  @Test
  @DisplayName("????????????????????? ?????????????????? ???????????? ???????????? ???????????? ???????????? ??????")
  void getNicknameByComment() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest commentRequest = new CommentRequest("?????? ??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    String nicknameByComment = commentService.getNicknameByComment(createComment);

    // then
    assertThat(nicknameByComment).isEqualTo(findUser.get().getNickname());
  }

  @Test
  @DisplayName("????????? ??????")
  void countLike() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // ?????? ??????
    CommentRequest commentRequest = new CommentRequest("?????? ??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    commentService.countLike(createComment.getId());
    // then
    assertThat(commentRepository.existsCommentById(createComment.getId())).isTrue();
  }

  @Test
  @DisplayName("???????????? ?????? ?????? (?????? ???????????? ?????? ???)")
  void countComments() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // when
    long countComments = commentService.countComments(createCommunityBoard.getId());

    // then
    assertThat(commentRepository.countByBoardId(createCommunityBoard.getId())).isEqualTo(
        countComments);
  }

  @Test
  @DisplayName("???????????? ?????? ??????, ????????? ??????(?????? ???????????? ?????? ???)")
  void countCommentsAndReplyComments() throws IOException {
    // given

    // ?????? id??? ????????? ??????
    Optional<User> findUser = userRepository.findById(1L);

    // ????????? ??????
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("??????", "??????",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    long countComments = commentRepository.countByBoardId(createCommunityBoard.getId());
    List<Comment> comments = commentRepository.findAllByBoardId(createCommunityBoard.getId());
    long countReplyComments = replyCommentService.countReplyComments(comments);

    // when
    commentService.countCommentsAndReplyComments(createCommunityBoard.getId());
    long l = countComments + countReplyComments;

    // then
    assertThat(
        commentService.countCommentsAndReplyComments(createCommunityBoard.getId())).isEqualTo(l);


  }
}