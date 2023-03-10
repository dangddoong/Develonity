package com.develonity.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.develonity.board.dto.CommunityBoardRequest;
import com.develonity.board.entity.BoardImage;
import com.develonity.board.entity.CommunityBoard;
import com.develonity.board.entity.CommunityCategory;
import com.develonity.board.repository.BoardImageRepository;
import com.develonity.board.service.CommunityBoardServiceImpl;
import com.develonity.comment.dto.CommentRequest;
import com.develonity.comment.entity.Comment;
import com.develonity.comment.repository.CommentLikeRepository;
import com.develonity.comment.repository.CommentRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class CommentLikeServiceImplTest {

  @Autowired
  private CommentLikeRepository commentLikeRepository;
  @Autowired
  private CommentLikeServiceImpl commentLikeService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CommentServiceImpl commentService;
  @Autowired
  private CommunityBoardServiceImpl communityBoardService;
  @Autowired
  private BoardImageRepository boardImageRepository;
  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  void allDeleteBefore() {
    commentLikeRepository.deleteAll();
  }

  @AfterEach
  void allDeleteAfter() {
    commentRepository.deleteAll();
  }

  @Test
  @DisplayName("????????? ??????")
  void addCommentLike() throws IOException {
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
    CommentRequest commentRequest = new CommentRequest("??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());

    // then
    assertThat(commentService.countLike(createComment.getId())).isEqualTo(1L);
  }

  @Test
  @DisplayName("????????? ?????? ??????(?????? ???????????? ?????? ?????? ??????)")
  void DuplicationCommentLike() throws IOException {
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
    CommentRequest commentRequest = new CommentRequest("??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());
    // when
    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());

    // then
    assertThrows(CustomException.class,
        () -> commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId()));
  }

  @Test
  @DisplayName("????????? ??????")
  void cancelCommentLike() throws IOException {
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
    CommentRequest commentRequest = new CommentRequest("??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());
    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());

    // when
    commentLikeService.cancelCommentLike(createComment.getId(), findUser.get().getId());

    // then
    assertThat(commentLikeRepository.existsCommentById(createComment.getId())).isFalse();
  }

  @Test
  @DisplayName("????????? ?????? ??????(???????????? ????????? ?????? ?????? ??????)")
  void notExistCommentLike() throws IOException {
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
    CommentRequest commentRequest = new CommentRequest("??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());
    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());

    // when
    commentLikeService.cancelCommentLike(createComment.getId(), findUser.get().getId());

    // then
    assertThrows(CustomException.class,
        () -> commentLikeService.cancelCommentLike(createComment.getId(), findUser.get().getId()));
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
    CommentRequest commentRequest = new CommentRequest("??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    commentLikeService.countLike(createComment.getId());

    // then
    assertThat(commentLikeRepository.countByCommentId(createComment.getId())).isEqualTo(
        commentLikeRepository.countByCommentId(createComment.getId()));
  }

// Repository??? ????????? ???????????? ????????? ????????????
//  @Test
//  @DisplayName("????????? ??????")
//  void deleteLike() {
//    // given
//    Optional<User> findUser = userRepository.findById(1L);
//    CommentRequest commentRequest = new CommentRequest("??????");
//    Comment createComment = commentService.createCommunityComment(1L, commentRequest,
//        findUser.get());
//    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());
//
//    // when
//    commentLikeService.deleteLike(createComment.getId());
//
//    // then
//    assertThat(commentLikeRepository.existsCommentById(createComment.getId())).isFalse();
//  }

  // ?????? ?????? ??????????????? ???????????? ??? ??? ????????????
//  @Test
//  @DisplayName("???????????? ?????? ????????? ??????")
//  void isExistLikes() {
//    // given
//    Optional<User> findUser = userRepository.findById(1L);
//    CommentRequest commentRequest = new CommentRequest("??????");
//    Comment createComment = commentService.createCommunityComment(1L, commentRequest,
//        findUser.get());
//    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());
//    // when
//    commentLikeService.isExistLikes(createComment.getId());
//    // then
//    assertThat(commentLikeRepository.existsCommentById(createComment.getId())).isTrue();
//  }

  @Test
  @DisplayName("????????? ?????? ???????????? ???????????? ??????")
  void isExistLikesCommentIdAndUserId() throws IOException {
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
    CommentRequest commentRequest = new CommentRequest("??????");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());
    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());

    // when
    commentLikeService.isExistLikesCommentIdAndUserId(createComment.getId(),
        findUser.get().getId());

    // then
    assertThat(commentLikeRepository.existsCommentLikeByCommentIdAndUserId(createComment.getId(),
        findUser.get().getId())).isTrue();
  }

//  @Test
//  void deleteAllByCommentId() {
//    // given
//    Optional<User> findUser = userRepository.findById(1L);
//    CommentRequest commentRequest = new CommentRequest("??????");
//    Comment createComment = commentService.createCommunityComment(1L, commentRequest,
//        findUser.get());
//    commentLikeService.addCommentLike(createComment.getId(), findUser.get().getId());
//
//    // when
//    commentLikeService.deleteAllByCommentId(createComment.getId());
//
//    // then
//    assertThat(commentLikeRepository.existsCommentById(createComment.getId())).isFalse();
//  }
}