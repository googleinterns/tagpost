import {Injectable} from '@angular/core';

import {Observable, Subject} from 'rxjs';

import {Comment, Tag, TagStats, Thread} from 'compiled_proto/src/proto/tagpost_pb';
import {TagpostServiceClient} from 'compiled_proto/src/proto/Tagpost_rpcServiceClientPb';
import {
  AddCommentUnderThreadRequest,
  AddCommentUnderThreadResponse,
  AddThreadWithTagRequest,
  AddThreadWithTagResponse,
  FetchCommentsUnderThreadRequest,
  FetchCommentsUnderThreadResponse,
  FetchThreadsByTagRequest,
  FetchThreadsByTagResponse,
  GetTagStatsRequest,
  GetTagStatsResponse
} from 'compiled_proto/src/proto/tagpost_rpc_pb';
import {environment} from 'environments/environment';

/**
 * A data service that communicate with backend services.
 */
@Injectable({
  providedIn: 'root'
})
export class DataService {
  private client: TagpostServiceClient;

  private threadListSource = new Subject<Array<Thread>>();
  public readonly threadList: Observable<Array<Thread>> = this.threadListSource.asObservable();

  private commentListSource = new Subject<Array<Comment>>();
  public readonly commentList: Observable<Array<Comment>> = this.commentListSource.asObservable();

  private tagStatsSource = new Subject<TagStats>();
  public readonly tagStats: Observable<TagStats> = this.tagStatsSource.asObservable();

  constructor() {
    this.client = new TagpostServiceClient(environment.apiProxy);
  }

  /**
   * Fetch a list of thread with given tag name.
   * If success, Multicast the newly fetched thread list to all threadList observers
   */
  fetchThreads(tag: string): void {
    // clear threadListSource before fetching threads
    this.clearThreadList();

    const req = new FetchThreadsByTagRequest();
    req.setTag(tag);
    this.client.fetchThreadsByTag(req, {}, (err, response: FetchThreadsByTagResponse) => {
      if (err) {
        console.error(err.code, err.message);
        alert(err.message);
      }
      if (response) {
        this.threadListSource.next(response.getThreadsList());
      }
    });
  }

  /**
   * Add a new thread with given tag name.
   */
  addThread(tag: string, topic: string): Promise<Thread> {
    return new Promise<Thread>(((resolve, reject) => {
      const primaryTag = new Tag();
      primaryTag.setTagName(tag);

      const newThread = new Thread();
      newThread.setPrimaryTag(primaryTag);
      newThread.setTopic(topic);

      const req = new AddThreadWithTagRequest();
      req.setThread(newThread);

      this.client.addThreadWithTag(req, {}, (err, response: AddThreadWithTagResponse) => {
        if (err) {
          console.error(err.code, err.message);
          reject(err);
        }
        if (response) {
          resolve(response.getThread());
        }
      });
    }));
  }

  /**
   * Fetch a list of comments with given threadId.
   * If success, Multicast the newly fetched comment list to all commentList observers
   */
  fetchComments(threadId: string): void {
    const req = new FetchCommentsUnderThreadRequest();
    req.setThreadId(threadId);
    this.client.fetchCommentsUnderThread(req, {}, (err, response: FetchCommentsUnderThreadResponse) => {
      if (err) {
        console.error(err.code, err.message);
        alert(err.message);
      }
      if (response) {
        this.commentListSource.next(response.getCommentList());
      }
    });
  }

  /**
   * Add a new comment under a specified thread.
   */
  addComment(threadId: string, username: string, content: string, primaryTag: string, extraTags: string[]): Promise<Comment> {
    return new Promise<Comment>(((resolve, reject) => {

      const newComment = new Comment();
      newComment.setThreadId(threadId);
      newComment.setUsername(username);
      newComment.setCommentContent(content);

      const tag = new Tag();
      tag.setTagName(primaryTag);
      newComment.setPrimaryTag(tag);

      const extraTagsList: Array<Tag> = extraTags.map(tagName  => {
        const extraTag = new Tag();
        extraTag.setTagName(tagName);
        return extraTag;
      });
      newComment.setExtraTagsList(extraTagsList);

      const req = new AddCommentUnderThreadRequest();
      req.setComment(newComment);

      this.client.addCommentUnderThread(req, {}, (err, response: AddCommentUnderThreadResponse) => {
        if (err) {
          console.error(err.code, err.message);
          reject(err);
        }
        if (response) {
          resolve(response.getComment());
        }
      });
    }));
  }

  /**
   * Fetch Tag Statistics with given tag
   * If success, Multicast the newly fetched tagStats to all tagStats observers
   */
  fetchTagStats(tag: string): void {
    const req = new GetTagStatsRequest();
    req.setTag(tag);
    this.client.getTagStats(req, {}, (err, response: GetTagStatsResponse) => {
      if (err) {
        console.error(err.code, err.message);
        alert(err.message);
      }
      if (response) {
        this.tagStatsSource.next(response.getStats());
      }
    });
  }

  clearThreadList(): void {
    this.threadListSource.next(void Array<Thread>());
  }
}
