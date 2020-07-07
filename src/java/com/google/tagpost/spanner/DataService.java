package com.google.tagpost.spanner;
import com.google.tagpost.Comment;
import com.google.tagpost.Thread;

import java.util.List;

public interface DataService {

  List<Thread> getAllThreadsByTag(String tag);

  Thread addNewThreadWithTag(Thread thread) throws DataException;

  List<Comment> getAllCommentsByThreadId(String threadId);

  Comment addNewCommentUnderThread(Comment comment) throws DataException;
}
