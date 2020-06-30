package com.google.tagpost.spanner;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.common.collect.ImmutableList;
import com.google.inject.Singleton;
import com.google.tagpost.Comment;
import com.google.tagpost.Tag;
import com.google.tagpost.Thread;

import java.util.UUID;
import java.util.stream.Collectors;

/** Data access and operations of Spanner */
@Singleton
public class SpannerService implements DataService {

  private DatabaseClient dbClient;
  private DatabaseId db;
  private Spanner spanner;

  public SpannerService() {
    initDb();
    dbClient = spanner.getDatabaseClient(db);
  }

  @Override
  public ImmutableList<Thread> getAllThreadsByTag(String tag) {

    ImmutableList<Thread> threadList;

    String SQLStatement = "SELECT ThreadID, PrimaryTag FROM Thread WHERE PrimaryTag = @primaryTag";
    Statement statement = Statement.newBuilder(SQLStatement).bind("primaryTag").to(tag).build();

    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      threadList = convertResultToThreadList(resultSet);
    }
    return threadList;
  }

  @Override
  public Thread addNewThreadWithTag(String primaryTag, Thread thread) throws SpannerException {
    String threadId = UUID.randomUUID().toString();

    Mutation mutation =
        Mutation.newInsertBuilder("Thread")
            .set("PrimaryTag")
            .to(primaryTag)
            .set("ThreadID")
            .to(threadId)
            .build();

    dbClient.write(ImmutableList.of(mutation));
    thread = thread.toBuilder().setThreadId(threadId).build();
    return thread;
  }

  @Override
  public ImmutableList<Comment> getAllCommentsByThreadId(String threadId) {

    ImmutableList<Comment> commentList;

    String SQLStatement = "SELECT * FROM Comment WHERE ThreadID = @threadId";
    Statement statement = Statement.newBuilder(SQLStatement).bind("threadId").to(threadId).build();

    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      commentList = convertResultToCommentList(resultSet);
    }
    return commentList;
  }

  @Override
  public Comment addNewCommentUnderThread(Comment comment) throws SpannerException {
    String commentId = UUID.randomUUID().toString();
    Timestamp timestamp = Timestamp.now();

    Mutation mutation =
        Mutation.newInsertBuilder("Comment")
            .set("CommentID")
            .to(commentId)
            .set("Timestamp")
            .to(timestamp)
            .set("CommentContent")
            .to(comment.getCommentContent())
            .set("UserName")
            .to(comment.getUsername())
            .set("ThreadID")
            .to(comment.getThreadId())
            .set("PrimaryTag")
            .to(comment.getPrimaryTag().getTagName())
            .set("ExtraTags")
            .toStringArray(
                comment.getExtraTagsList().stream()
                    .map(s -> s.getTagName())
                    .collect(Collectors.toList()))
            .build();

    dbClient.write(ImmutableList.of(mutation));

    // complete commentId and timestamp fields in user specified comment
    comment = comment.toBuilder().setTimestamp(timestamp.toProto()).setCommentId(commentId).build();
    return comment;
  }

  /** Initialize database */
  private void initDb() {
    db = DatabaseId.of("testing-bigtest", "tagpost", "test");
    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    spanner = spannerOptions.getService();
  }

  private ImmutableList<Thread> convertResultToThreadList(ResultSet resultSet) {

    ImmutableList.Builder<Thread> threadListBuilder = ImmutableList.builder();

    while (resultSet.next()) {
      String threadId = resultSet.getString("ThreadID");
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
      comment.setThreadId(resultSet.getString("ThreadID"));
      comment.setCommentId(resultSet.getString("CommentID"));
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
