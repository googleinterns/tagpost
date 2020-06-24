package com.google.tagpost.spanner;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Statement;

import com.google.tagpost.Comment;
import com.google.tagpost.Tag;
import com.google.tagpost.Thread;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

/** Data access and operations of Spanner */
@Singleton
public class SpannerService implements DataService {

  @Override
  public List<Thread> getAllThreadsByTag(String tag) {

    String SQLStatement = "SELECT ThreadID, PrimaryTag FROM Thread WHERE PrimaryTag = @primaryTag";

    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    Spanner spanner = spannerOptions.getService();
    List<Thread> threadList = new ArrayList<>();

    try {
      DatabaseId db = DatabaseId.of("testing-bigtest", "tagpost", "test");
      DatabaseClient dbClient = spanner.getDatabaseClient(db);

      Statement statement =
          Statement.newBuilder(SQLStatement).bind("primaryTag").to(tag).build();

      ResultSet resultSet = dbClient.singleUse().executeQuery(statement);
      convertResultToThreadList(threadList, resultSet);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return threadList;
  }

  @Override
  public List<Comment> getAllCommentsByThreadId(long threadId) {

    String SQLStatement = "SELECT * FROM Comment WHERE ThreadID = @threadId";

    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    Spanner spanner = spannerOptions.getService();
    List<Comment> commentList = new ArrayList<>();

    try {
      DatabaseId db = DatabaseId.of("testing-bigtest", "tagpost", "test");
      DatabaseClient dbClient = spanner.getDatabaseClient(db);

      Statement statement =
          Statement.newBuilder(SQLStatement).bind("threadId").to(threadId).build();

      ResultSet resultSet = dbClient.singleUse().executeQuery(statement);
      convertResultToCommentList(threadId, commentList, resultSet);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return commentList;
  }

  private void convertResultToThreadList(List<Thread> threadList, ResultSet resultSet) {
    while (resultSet.next()) {
      long threadId = resultSet.getLong("ThreadID");
      Tag primaryTag = Tag.newBuilder().setTagName(resultSet.getString("PrimaryTag")).build();
      Thread thread = Thread.newBuilder().setThreadId(threadId).setPrimaryTag(primaryTag).build();
      threadList.add(thread);
    }
  }

  private void convertResultToCommentList(
      long threadId, List<Comment> commentList, ResultSet resultSet) {
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
