package com.mvp.common.tools.preferences;

public final class Preconditions {
  public static void checkNotNull(Object o, String message) {
    if (o == null) {
      throw new NullPointerException(message);
    }
  }

  private Preconditions() {
    throw new AssertionError("No instances");
  }
}
