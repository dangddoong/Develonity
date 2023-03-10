package com.develonity.comment.service;

import com.develonity.comment.dto.CommentPageDto;
import com.develonity.comment.dto.CommentRequest;
import com.develonity.comment.dto.CommentResponse;
import com.develonity.comment.dto.CommentSearchCond;
import com.develonity.comment.entity.Comment;
import com.develonity.user.entity.User;
import org.springframework.data.domain.Page;

public interface CommentService {

  Page<CommentResponse> getAllComment(CommentPageDto commentPageDto,
      CommentSearchCond commentSearchCond);

  // Querydsl 사용 X
//  Page<CommentResponse> getMyComments(CommentList commentList, Long userId,
//      User user);

  Page<CommentResponse> getMyComments(CommentPageDto commentPageDto,
      CommentSearchCond commentSearchCond, Long userId);

  Comment createQuestionComment(Long questionBoardId, CommentRequest requestDto, User user);

  Comment updateQuestionComment(Long commentId, CommentRequest request,
      User user);

  void deleteQuestionComment(Long commentId, User user);

  Comment createCommunityComment(Long communityBoardId, CommentRequest request, User user);

  Comment updateCommunityComment(Long commentId, CommentRequest request,
      User user);

  void deleteCommunity(Long commentId, User user);

  Comment getComment(Long commentId);

  String getNickname(Long userId);

  String getNicknameByComment(Comment comment);

  void adoptComment(Comment comment);

  void deleteCommentsByBoardId(Long boardId);

  boolean existsCommentByBoardIdAndUserId(Long boardId, Long userId);

  boolean existsCommentsByBoardId(Long boardId);

  //게시글에 달린 댓글 ,대댓글 조회
  Page<CommentResponse> getCommentsByBoard(Long boardId, User user);

  long countLike(Long commentId);

  long countCommentsAndReplyComments(Long boardId);

  long countComments(Long boardId);

}
