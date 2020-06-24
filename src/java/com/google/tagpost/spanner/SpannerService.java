package com.google.tagpost.spanner;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Statement;

import com.google.common.collect.ImmutableList;
import com.google.tagpost.Comment;
import com.google.tagpost.Tag;
import com.google.tagpost.Thread;

import com.google.inject.Singleton;

/** Data access and operations of Spanner */
@Singleton
public class SpannerService implements DataService {

  @Override
  public ImmutableList<Thread> getAllThreadsByTag(String tag) {

    ImmutableList<Thread> threadList;

    String SQLStatement = "SELECT ThreadID, PrimaryTag FROM Thread WHERE PrimaryTag = @primaryTag";
    Statement statement = Statement.newBuilder(SQLStatement).bind("primaryTag").to(tag).build();

    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    Spanner spanner = spannerOptions.getService();
    DatabaseId db = DatabaseId.of("testing-bigtest", "tagpost", "test");
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      threadList = convertResultToThreadList(resultSet);
    }
    return threadList;
  }

  @Override
  public ImmutableList<Comment> getAllCommentsByThreadId(long threadId) {

    ImmutableList<Comment> commentList;

    String SQLStatement = "SELECT * FROM Comment WHERE ThreadID = @threadId";
    Statement statement = Statement.newBuilder(SQLStatement).bind("threadId").to(threadId).build();

    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    Spanner spanner = spannerOptions.getService();
    DatabaseId db = DatabaseId.of("testing-bigtest", "tagpost", "test");
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      commentList = convertResultToCommentList(resultSet);
    }
    return commentList;
  }

  private ImmutableList<Thread> convertResultToThreadList(ResultSet resultSet) {

    ImmutableList.Builder<Thread> threadListBuilder = ImmutableList.builder();

    while (resultSet.next()) {
      long threadId = resultSet.getLong("ThreadID");
      Tag primaryTag = Tag.newBuilder().setTagName(resultSet.getString("PrimaryTag")).build();
      Thread thread = Thread.newBuilder().setThreadId(threadId).setPrimaryTag(primaryTag).build();
      threadListBuilder.add(thread);
    }
    return threadListBuilder.build();
  }

  private ImmutableList<Comment> convertResultToCommentList(ResultSet resultSet) {

    ImmutableList.Builder<Comment> commentListBuilder = ImmutableList.builder();

    while (resultSet.next()) {
      Comment.Builder comment = Comment.newBuilder();
      comment.setThreadId(resultSet.getLong("ThreadID"));
      comment.setCommentId(resultSet.getLong("CommentID"));
      comment.setCommentContent(resultSet.getString("CommentContent"));

      ImmutableList.Builder<Tag> extraTags = ImmutableList.builder();
      for (String tagName : resultSet.getStringList("ExtraTags")) {
        Tag tag = Tag.newBuilder().setTagName(tagName).build();
        extraTags.add(tag);
      }

      comment.addAllExtraTags(extraTags.build());
      comment.setUsername(resultSet.getString("Username"));
      comment.setPrimaryTag(Tag.newBuilder().setTagName(resultSet.getString("PrimaryTag")).build());
      comment.setTimestamp(resultSet.getTimestamp("Timestamp").toProto());
      commentListBuilder.add(comment.build());
    }
    return commentListBuilder.build();
  }
}
