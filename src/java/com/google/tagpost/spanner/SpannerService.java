package com.google.tagpost.spanner;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.*;
import com.google.tagpost.Comment;
import com.google.tagpost.Tag;
import com.google.tagpost.Thread;

import java.util.ArrayList;
import java.util.List;

/** Data access and operations of Spanner */
public class SpannerService {

  private static final SpannerService spannerService = new SpannerService() {};

  public static SpannerService create() {
    return spannerService;
  }

  public List<Thread> getAllThreadsByTag(Tag tag) {

    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    Spanner spanner = spannerOptions.getService();
    List<Thread> threadList = new ArrayList<>();

    try {
      DatabaseId db = DatabaseId.of("testing-bigtest", "tagpost", "test");
      DatabaseClient dbClient = spanner.getDatabaseClient(db);
      String query =
          "SELECT ThreadID, PrimaryTag FROM Thread WHERE PrimaryTag = '" + tag.getTagName() + "'";
      ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(query));
      convertResultToThreadList(threadList, resultSet);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return threadList;
  }

  private void convertResultToThreadList(List<Thread> threadList, ResultSet resultSet) {
    while (resultSet.next()) {
      long threadId = resultSet.getLong("ThreadID");
      Tag primaryTag = Tag.newBuilder().setTagName(resultSet.getString("PrimaryTag")).build();
      Thread thread = Thread.newBuilder().setThreadId(threadId).setPrimaryTag(primaryTag).build();
      threadList.add(thread);
    }
  }

  public List<Comment> getAllCommentsByThreadId(long threadId) {

    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    Spanner spanner = spannerOptions.getService();
    List<Comment> commentList = new ArrayList<>();

    try {
      DatabaseId db = DatabaseId.of("testing-bigtest", "tagpost", "test");
      DatabaseClient dbClient = spanner.getDatabaseClient(db);
      String query = "SELECT * FROM Comment WHERE ThreadID = " + threadId;
      ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(query));
      convertResultToCommentList(threadId, commentList, resultSet);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return commentList;
  }

  private void convertResultToCommentList(long threadId, List<Comment> commentList, ResultSet resultSet) {
    while (resultSet.next()) {
      long commentId = resultSet.getLong("CommentID");
      String username = resultSet.getString("Username");
      Timestamp timestamp = resultSet.getTimestamp("Timestamp");
      Tag primaryTag = Tag.newBuilder().setTagName(resultSet.getString("PrimaryTag")).build();
      List<Tag> extraTags = new ArrayList<>();
      if (!resultSet.isNull("ExtraTags")) {
        for (String tagName : resultSet.getStringList("ExtraTags")) {
          Tag tag = Tag.newBuilder().setTagName(tagName).build();
          extraTags.add(tag);
        }
      }
      String content = resultSet.getString("CommentContent");
      Comment comment =
          Comment.newBuilder()
              .setCommentId(commentId)
              .setCommentContent(content)
              .addAllExtraTags(extraTags)
              .setPrimaryTag(primaryTag)
              .setThreadId(threadId)
              .setUsername(username)
              .setTimestamp(timestamp.toProto())
              .build();
      commentList.add(comment);
    }
  }
}
