package pl.minecodes.metric.backend.util;

import jakarta.servlet.http.HttpServletRequest;

public class AddressUtil {

  private AddressUtil() {
  }

  public static String getRealRequestAddress(HttpServletRequest request) {
    String cloudFlareRequestAddress = request.getHeader("CF-Connecting-IP");
    String xForwardedForRequestAddress = request.getHeader("X-Forwarded-For");

    if (cloudFlareRequestAddress != null) {
      return cloudFlareRequestAddress;
    }

    if (xForwardedForRequestAddress != null) {
      return xForwardedForRequestAddress.split(",")[0].trim();
    }

    return request.getRemoteAddr();
  }
}
