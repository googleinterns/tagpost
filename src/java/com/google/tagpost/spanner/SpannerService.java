package com.google.tagpost.spanner;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.SpannerException;
import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;
import com.google.gson.Gson;
import com.google.tagpost.Comment;
import com.google.tagpost.Tag;
import com.google.tagpost.TagStats;
import com.google.tagpost.Thread;

import java.lang.reflect.Type;
import java.util.Map;
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

    String SQLStatement = "SELECT * FROM Thread WHERE PrimaryTag = @primaryTag";
    Statement statement = Statement.newBuilder(SQLStatement).bind("primaryTag").to(tag).build();

    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      threadList = convertResultToThreadList(resultSet);
    }
    return threadList;
  }

  @Override
  public Thread addNewThreadWithTag(Thread thread) {
    String threadId = UUID.randomUUID().toString();
    Timestamp timestamp = Timestamp.now();
    String tagName = thread.getPrimaryTag().getTagName();

    // Add a new tag entry in Tag table if tagName not yet existed
    Mutation tagMutation = Mutation.newInsertBuilder("Tag").set("TagName").to(tagName).build();
    try {
      dbClient.write(ImmutableList.of(tagMutation));
    } catch (SpannerException ok) {
      // SpannerException is expected to be caught here for adding an already existed TagName
    }

    Mutation mutation =
        Mutation.newInsertBuilder("Thread")
            .set("PrimaryTag")
            .to(thread.getPrimaryTag().getTagName())
            .set("ThreadID")
            .to(threadId)
            .set("Topic")
            .to(thread.getTopic())
            .set("Timestamp")
            .to(timestamp)
            .build();

    dbClient.write(ImmutableList.of(mutation));
    return thread.toBuilder().setThreadId(threadId).build();
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
  public Comment addNewCommentUnderThread(Comment comment) {
    String commentId = UUID.randomUUID().toString();
    Timestamp timestamp = Timestamp.now();

    // retrieve primaryTagName from Thread table
    String primaryTag = "";
    String SQLStatement = "SELECT PrimaryTag FROM Thread WHERE ThreadID = @threadId";
    Statement statement =
        Statement.newBuilder(SQLStatement).bind("threadId").to(comment.getThreadId()).build();

    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        primaryTag = resultSet.getString("PrimaryTag");
      }
    }

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
            .to(primaryTag)
            .set("ExtraTags")
            .toStringArray(
                comment.getExtraTagsList().stream()
                    .map(s -> s.getTagName())
                    .collect(Collectors.toList()))
            .build();

    dbClient.write(ImmutableList.of(mutation));

    // complete commentId and timestamp fields in user specified comment
    return comment.toBuilder().setTimestamp(timestamp.toProto()).setCommentId(commentId).build();
  }

  @Override
  public TagStats getTagStats(String tag) {
    TagStats stats;

    String SQLStatement = "SELECT * FROM TagStats WHERE PrimaryTag = @tag";
    Statement statement = Statement.newBuilder(SQLStatement).bind("tag").to(tag).build();

    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      stats = convertResultToTagStats(resultSet);
    }
    return stats;
  }

  /** Initialize database */
  private void initDb() {
    db = DatabaseId.of("testing-bigtest", "tagpost", "test");
    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    spanner = spannerOptions.getService();
  }

  private static ImmutableList<Thread> convertResultToThreadList(ResultSet resultSet) {

    ImmutableList.Builder<Thread> threadListBuilder = ImmutableList.builder();

    while (resultSet.next()) {
      Thread.Builder thread = Thread.newBuilder();
      thread.setThreadId(resultSet.getString("ThreadID"));
      thread.setPrimaryTag(Tag.newBuilder().setTagName(resultSet.getString("PrimaryTag")).build());
      thread.setTopic(resultSet.getString("Topic"));
      thread.setTimestamp(resultSet.getTimestamp("Timestamp").toProto());
      threadListBuilder.add(thread.build());
    }
    return threadListBuilder.build();
  }

  private static ImmutableList<Comment> convertResultToCommentList(ResultSet resultSet) {

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

  private static TagStats convertResultToTagStats(ResultSet resultSet) {
    TagStats.Builder stats = TagStats.newBuilder();
    while (resultSet.next()) {
      String jsonStats = resultSet.getString("Statistics");

      // Parse a JSON string into Map
      Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
      Map<String, Integer> statsMap = new Gson().fromJson(jsonStats, mapType);

      stats.putAllStatistics(statsMap);
      stats.setTag(Tag.newBuilder().setTagName(resultSet.getString("PrimaryTag")).build());
    }
    return stats.build();
  }
}
