package com.develonity.comment.dto;

import com.develonity.comment.entity.Comment;
import com.develonity.comment.entity.ReplyComment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CommentResponse {

  private final Long id;

  private final String nickname;
  private final String content;
  private final int commentLike;
  private final LocalDateTime createdAt;

  private final LocalDateTime lastModifiedAt;

  private List<ReplyCommentResponse> replyCommentList;


  public CommentResponse(Comment comment, String nickname, int commentLike) {
    List<ReplyCommentResponse> list = new ArrayList<>();
    this.id = comment.getId();
    this.nickname = nickname;
    this.content = comment.getContent();
    this.commentLike = commentLike;
    this.createdAt = comment.getCreatedDate();
    this.lastModifiedAt = comment.getLastModifiedDate();
    for (ReplyComment replyComment : comment.getReplyCommentList) {
      list.add(new ReplyCommentResponse(replyComment, nickname));
    }
    this.replyCommentList = list;
  }

//  public static CommentResponse toCommentResponseDto(Comment comment, String nickname,
//      int commentLike) {
//    return CommentResponse.builder()
//        .id(comment.getId())
//        .nickname(nickname)
//        .content(comment.getContent())
//        .commentLike(commentLike)
//        .point(comment.getPoint())
//        .createdAt(comment.getCreatedDate())
//        .lastModifiedAt(comment.getLastModifiedDate())
//        .build();
//  }

}
