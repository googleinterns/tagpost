package com.google.tagpost.spanner;

/** The DataException class wraps runtime exceptions occur during the operations of DataService */
public class DataException extends RuntimeException {
  public DataException() {}

  public DataException(String msg) {
    super(msg);
  }

  public DataException(Throwable cause) {
    super(cause);
  }

  public DataException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
