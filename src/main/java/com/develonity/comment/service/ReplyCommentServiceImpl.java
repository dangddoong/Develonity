package com.develonity.comment.service;

import com.develonity.comment.dto.ReplyCommentRequest;
import com.develonity.comment.dto.ReplyCommentResponse;
import com.develonity.comment.entity.Comment;
import com.develonity.comment.entity.ReplyComment;
import com.develonity.comment.repository.CommentRepository;
import com.develonity.comment.repository.ReplyCommentRepository;
import com.develonity.common.exception.CustomException;
import com.develonity.common.exception.ExceptionStatus;
import com.develonity.user.entity.User;
import com.develonity.user.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyCommentServiceImpl implements ReplyCommentService {

  private final CommentRepository commentRepository;
  private final ReplyCommentRepository replyCommentRepository;
  private final UserService userService;

  // 댓글 확인
  private Comment getComment(Long commentId) {
    return commentRepository.findById(commentId).orElseThrow(
        () -> new CustomException(ExceptionStatus.COMMENT_IS_NOT_EXIST)
    );
  }

  // 대댓글 확인
  private ReplyComment getReplyComment(Long replyCommentId) {
    return replyCommentRepository.findById(replyCommentId).orElseThrow(
        () -> new CustomException(ExceptionStatus.REPLY_COMMENT_IS_NOT_EXIST)
    );
  }

  // 댓글 작성자 확인
  public void checkUser(User user, ReplyComment replyComment) {
    if (!user.getNickname().equals(getNicknameByReplyComment(replyComment))) {
      throw new CustomException(ExceptionStatus.REPLY_COMMENT_USER_NOT_MATCH);
    }
  }

  // 대댓글 작성
  @Override
  @Transactional
  public ReplyComment createReplyComment(Long commentId, ReplyCommentRequest request, User user) {
    // 댓글이 있는지 확인
    Comment comment = getComment(commentId);

    // 대댓글 작성
    ReplyComment replyComment = new ReplyComment(user, request, comment);
    replyCommentRepository.save(replyComment);
    return replyComment;
  }

  // 대댓글 수정
  @Override
  @Transactional
  public ReplyComment updateReplyComment(Long commentId, Long replyCommentId,
      ReplyCommentRequest request,
      User user) {
    // 댓글이 있는지 확인
    Comment comment = getComment(commentId);
    // 대댓글이 있는지 확인
    ReplyComment replyComment = getReplyComment(replyCommentId);
    // 작성자가 맞는지 확인
    checkUser(user, replyComment);
    // 댓글 수정
    replyComment.updateReplyComment(request.getContent());
    replyCommentRepository.save(replyComment);
    return replyComment;
  }

  // 대댓글 삭제
  @Override
  @Transactional
  public void deleteReplyComment(Long replyCommentId, User user) {
    // 대댓글이 있는지 확인
    ReplyComment replyComment = getReplyComment(replyCommentId);
    // 작성자가 맞는지 확인
    checkUser(user, replyComment);
    // 댓글 삭제
    replyCommentRepository.delete(replyComment);
  }


  // 닉네임을 가져오는 기능
  @Override
  public String getNickname(Long userId) {
    return userService.getProfile(userId).getNickname();
  }

  @Override
  public String getNicknameByReplyComment(ReplyComment replyComment) {
    return userService.getProfile(replyComment.getUserId()).getNickname();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReplyCommentResponse> getReplyCommentResponses(Comment comment) {

    List<ReplyComment> replyComments = replyCommentRepository.findAllByComment(comment);
    List<Long> userIds = new ArrayList<>();
    List<ReplyCommentResponse> replyCommentResponses = new ArrayList<>();

    for (ReplyComment replyComment : replyComments) {
      Long userId = replyComment.getUserId();
      userIds.add(userId);
    }
    HashMap<Long, String> userIdAndNickname = userService.getUserIdAndNickname(userIds);

    for (ReplyComment replyComment : replyComments) {
      ReplyCommentResponse replyCommentResponse = new ReplyCommentResponse(replyComment,
          userIdAndNickname.get(replyComment.getUserId()));
      replyCommentResponses.add(replyCommentResponse);
    }
    return replyCommentResponses;
  }

  @Transactional
  @Override
  public void deleteAllReplyComments(Comment comment) {
    replyCommentRepository.deleteAllByCommentId(comment);
  }

  @Override
  public long countReplyComments(List<Comment> comments) {
    return replyCommentRepository.countAllByCommentIn(comments);
  }
}
