package com.github.ynverxe.littleheads.exception;

public class InvalidLittleHeadModel extends RuntimeException {

  public InvalidLittleHeadModel(String message, String name) {
    super("Model: " + name + " reason: " + message);
  }

  public InvalidLittleHeadModel(String message, String name, Throwable cause) {
    super("Model: " + name + " reason: " + message, cause);
  }
}