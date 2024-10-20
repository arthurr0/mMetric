package pl.minecodes.metric.backend.util;

import java.util.Random;

public class KeyUtil {

  private static final Random RANDOM = new Random();
  private static final int KEY_LENGTH = 32;
  private static final String CHARTS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

  private KeyUtil() {
  }

  public static String keyGenerator() {
    StringBuilder key = new StringBuilder();
    for (int i = 0; i < KEY_LENGTH; i++) {
      key.append(CHARTS.charAt(RANDOM.nextInt(CHARTS.length())));
    }
    return key.toString();
  }
}
