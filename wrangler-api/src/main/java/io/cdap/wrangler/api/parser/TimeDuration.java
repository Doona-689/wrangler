package io.cdap.wrangler.api.parser;

public class TimeDuration extends Token {
  private final long milliseconds;

  public TimeDuration(String value) {
    super(value);
    this.milliseconds = parseTime(value);
  }

  private long parseTime(String input) {
    input = input.trim().toLowerCase();
    double num = Double.parseDouble(input.replaceAll("[^0-9.]", ""));
    if (input.endsWith("ms")) return (long) num;
    if (input.endsWith("s")) return (long) (num * 1000);
    if (input.endsWith("m")) return (long) (num * 60 * 1000);
    if (input.endsWith("h")) return (long) (num * 60 * 60 * 1000);
    return (long) num;
  }

  public long getMilliseconds() {
    return milliseconds;
  }
}
