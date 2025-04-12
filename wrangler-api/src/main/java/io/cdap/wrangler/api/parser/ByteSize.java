package io.cdap.wrangler.api.parser;

public class ByteSize extends Token {
  private final long bytes;

  public ByteSize(String value) {
    super(value);
    this.bytes = parseBytes(value);
  }

  private long parseBytes(String input) {
    input = input.trim().toUpperCase();
    double num = Double.parseDouble(input.replaceAll("[^0-9.]", ""));
    if (input.endsWith("KB")) return (long) (num * 1024);
    if (input.endsWith("MB")) return (long) (num * 1024 * 1024);
    if (input.endsWith("GB")) return (long) (num * 1024 * 1024 * 1024);
    if (input.endsWith("TB")) return (long) (num * 1024L * 1024 * 1024 * 1024);
    return (long) num; // Default to bytes
  }

  public long getBytes() {
    return bytes;
  }
}
