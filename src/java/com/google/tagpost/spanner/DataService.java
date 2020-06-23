package com.google.tagpost.spanner;

import com.google.tagpost.Comment;
import com.google.tagpost.Tag;
import com.google.tagpost.Thread;

import java.util.List;

public interface DataService {

  List<Thread> getAllThreadsByTag(Tag tag);

  List<Comment> getAllCommentsByThreadId(long threadId);
}
