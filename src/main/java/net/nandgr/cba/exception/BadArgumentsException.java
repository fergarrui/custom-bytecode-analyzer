package net.nandgr.cba.exception;

public class BadArgumentsException extends Exception {

  public BadArgumentsException(String message) {
    super(message);
  }

  public BadArgumentsException(String message, Throwable cause) {
    super(message, cause);
  }
}
