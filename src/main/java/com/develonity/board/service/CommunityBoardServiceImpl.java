package com.develonity.board.service;

import com.develonity.board.dto.BoardPage;
import com.develonity.board.dto.CommunityBoardRequest;
import com.develonity.board.dto.CommunityBoardResponse;
import com.develonity.board.entity.BoardImage;
import com.develonity.board.entity.CommunityBoard;
import com.develonity.board.entity.CommunityCategory;
import com.develonity.board.repository.BoardImageRepository;
import com.develonity.board.repository.BoardRepository;
import com.develonity.board.repository.CommunityBoardRepository;
import com.develonity.comment.service.CommentService;
import com.develonity.common.exception.CustomException;
import com.develonity.common.exception.ExceptionStatus;
import com.develonity.user.entity.User;
import com.develonity.user.service.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class CommunityBoardServiceImpl implements CommunityBoardService {

  private final CommunityBoardRepository communityBoardRepository;

  private final BoardLikeService boardLikeService;
  private final BoardImageRepository boardImageRepository;

  private final UserService userService;

  private final CommentService commentService;

  private final ScrapService scrapService;

  private final AwsS3Service awsS3Service;

  private final BoardRepository boardRepository;

  //잡담 게시글 생성(+이미지)
  @Override
  @Transactional
  public void createCommunityBoard(CommunityBoardRequest request,
      List<MultipartFile> multipartFiles,
      User user) throws IOException {
    CommunityBoard communityBoard = CommunityBoard.builder()
        .userId(user.getId())
        .title(request.getTitle())
        .content(request.getContent())
        .communityCategory(request.getCommunityCategory())
        .build();
    communityBoardRepository.save(communityBoard);

    if (multipartFiles != null) {
      upload(multipartFiles, communityBoard);
    }

  }


  //잡담 게시글 수정(+이미지)
  @Override
  @Transactional
  public void updateCommunityBoard(Long boardId, List<MultipartFile> multipartFiles,
      CommunityBoardRequest request, User user) throws IOException {
    CommunityBoard communityBoard = getCommunityBoardAndCheck(boardId);
    checkUser(communityBoard, user.getId());
    if (multipartFiles != null) {
      for (MultipartFile multipartFile : multipartFiles) {
        if (!multipartFile.isEmpty()) {
          deleteBoardImages(boardId);
          upload(multipartFiles, communityBoard);
        } else {
          upload(multipartFiles, communityBoard);
        }
      }
    }
    communityBoard.updateBoard(request.getTitle(), request.getContent());
    communityBoardRepository.save(communityBoard);
  }

  //잡담 게시글 삭제
  @Override
  @Transactional
  public void deleteCommunityBoard(Long boardId, User user) {
    CommunityBoard communityBoard = getCommunityBoardAndCheck(boardId);
    checkUser(communityBoard, user.getId());
    boardLikeService.deleteLike(boardId);
    deleteBoardImages(boardId);
    commentService.deleteCommentsByBoardId(boardId);
    scrapService.deleteScraps(boardId);
    communityBoardRepository.deleteById(boardId);


  }

  //잡담 게시글 전체 조회
  @Override
  @Transactional(readOnly = true)
  public Page<CommunityBoardResponse> getCommunityBoardPage(User user,
      BoardPage communityBoardPage) {

    Page<CommunityBoard> communityBoardPages = communityBoardRepository.findByCommunityCategoryAndTitleContainingOrContentContaining(
        communityBoardPage.getCommunityCategory(),
        communityBoardPage.getTitle(),
        communityBoardPage.getContent(),
        communityBoardPage.toPageable());

    return communityBoardPages.map(
        communityBoard -> CommunityBoardResponse.toCommunityBoardResponse(communityBoard,
            getNicknameByCommunityBoard(communityBoard)));
  }

  //테스트용전체조회
  @Override
  public Page<CommunityBoardResponse> getTestCommunityBoardPage(User user,
      BoardPage communityBoardPage) {

    Page<CommunityBoard> communityBoardPages = communityBoardRepository.findByCommunityCategory(
        communityBoardPage.getCommunityCategory(),
        communityBoardPage.toPageable());

    return communityBoardPages.map(
        communityBoard -> CommunityBoardResponse.toCommunityBoardResponse(communityBoard,
            getNicknameByCommunityBoard(communityBoard)));
  }

  //잡담 게시글 선택 조회
  @Override
  @Transactional(readOnly = true)
  public CommunityBoardResponse getCommunityBoard(Long boardId, User user) {
    CommunityBoard communityBoard = getCommunityBoardAndCheck(boardId);
    boolean hasLike = boardLikeService.existsLikesBoardIdAndUserId(boardId, user.getId());
    Long boardUserId = communityBoard.getUserId();
    String nickname = getNickname(boardUserId);

    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(boardId);
//    boardImageList.stream().map(boardImage -> boardImage.getImagePath())
//        .collect(Collectors.toList());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }
    return new CommunityBoardResponse(communityBoard, nickname, countLike(boardId), hasLike,
        imagePaths);
  }

  @Override
  public void checkUser(CommunityBoard communityBoard, Long userId) {
    if (!communityBoard.isWriter(userId)) {
      throw new CustomException(ExceptionStatus.BOARD_USER_NOT_MATCH);
    }
  }

  //등급 변경
  @Override
  public void upgradeGrade(Long userId, Long boardId) {
    if (userService.isLackedRespectPoint(userId)) {
      throw new CustomException(ExceptionStatus.POINTS_IS_LACKING);
    }
    userService.upgradeGrade(userId);

  }

  @Override
  public CommunityBoard getCommunityBoardAndCheck(Long boardId) {
    return communityBoardRepository.findById(boardId)
        .orElseThrow(() -> new CustomException(ExceptionStatus.BOARD_IS_NOT_EXIST));
  }

  @Override
  public int countLike(Long boardId) {
    return boardLikeService.countLikes(boardId);
  }

  @Override
  public Boolean ExistsBoard(Long boardId) {
    return communityBoardRepository.existsBoardById(boardId);
  }

  @Override
  public boolean isGradeBoard(Long boardId) {
    return communityBoardRepository.existsCommunityBoardByIdAndCommunityCategory(boardId,
        CommunityCategory.GRADE);
  }

  @Override
  public String getNickname(Long userId) {
    return userService.getProfile(userId).getNickname();
  }

  @Override
  public String getNicknameByCommunityBoard(CommunityBoard communityBoard) {
    return userService.getProfile(communityBoard.getUserId()).getNickname();
  }

  @Override
  public List<String> getImagePaths(CommunityBoard communityBoard) {
    List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(communityBoard.getId());
    List<String> imagePaths = new ArrayList<>();
    for (BoardImage boardImage : boardImageList) {
      imagePaths.add(boardImage.getImagePath());
    }
    return imagePaths;
  }

  @Override
  @Transactional
  public void upload(List<MultipartFile> multipartFiles, CommunityBoard communityBoard)
      throws IOException {

    List<String> uploadImagePaths = new ArrayList<>();
    String dir = "/board/communityImage";
    boolean exists = false;
    for (MultipartFile multipartFile : multipartFiles) {
      if (!multipartFile.isEmpty()) {
        exists = true;
      }
    }
    if (exists) {
      uploadImagePaths = awsS3Service.upload(multipartFiles, dir);
    }

    for (String imagePath : uploadImagePaths) {
      BoardImage boardImage = new BoardImage(imagePath, communityBoard.getId());
      boardImageRepository.save(boardImage);
    }
  }


  @Override
  @Transactional
  public void deleteBoardImages(Long boardId) {
    List<BoardImage> boardImages = boardImageRepository.findAllByBoardId(boardId);

    List<String> imagePaths = new ArrayList<>();

    for (BoardImage boardImage : boardImages) {
      imagePaths.add(boardImage.getImagePath());
    }
    for (String imagePath : imagePaths) {
      awsS3Service.deleteFile(imagePath);
    }
    boardImageRepository.deleteAllByBoardId(boardId);
  }
}
