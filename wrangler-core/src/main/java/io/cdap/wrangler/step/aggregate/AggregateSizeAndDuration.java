package io.cdap.wrangler.steps.aggregate;

import io.cdap.wrangler.api.AggregationDirective;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.SkipRowException;
import io.cdap.wrangler.api.executor.ExecutorContext;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.TextUsageDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregates byte size and time duration columns.
 */
@AggregationDirective(
  name = "aggregate-size-duration",
  usage = "aggregate-size-duration <sizeColumn> <timeColumn> <targetSizeCol> <targetTimeCol> [sizeUnit] [timeUnit] [aggregationType]"
)
public class AggregateSizeAndDuration implements Directive {
  private String sourceSizeCol;
  private String sourceTimeCol;
  private String targetSizeCol;
  private String targetTimeCol;
  private String sizeUnit = "B"; // default: bytes
  private String timeUnit = "ns"; // default: nanoseconds
  private String aggregationType = "total"; // or "average"

  private long totalSize = 0;
  private long totalTime = 0;
  private long count = 0;

  @Override
  public UsageDefinition define() {
    return new TextUsageDefinition("aggregate-size-duration <sizeColumn> <timeColumn> <targetSizeCol> <targetTimeCol> [sizeUnit] [timeUnit] [aggregationType]");
  }

  @Override
  public void initialize(Arguments args) {
    sourceSizeCol = args.value("sizeColumn");
    sourceTimeCol = args.value("timeColumn");
    targetSizeCol = args.value("targetSizeCol");
    targetTimeCol = args.value("targetTimeCol");

    if (args.contains("sizeUnit")) {
      sizeUnit = args.value("sizeUnit");
    }
    if (args.contains("timeUnit")) {
      timeUnit = args.value("timeUnit");
    }
    if (args.contains("aggregationType")) {
      aggregationType = args.value("aggregationType").toLowerCase();
    }
  }

  @Override
  public List<Row> execute(ExecutorContext ctx, Row row) throws SkipRowException {
    Object sizeVal = row.getValue(sourceSizeCol);
    Object timeVal = row.getValue(sourceTimeCol);

    long size = parseBytes(sizeVal.toString());
    long time = parseTime(timeVal.toString());

    totalSize += size;
    totalTime += time;
    count++;

    return Collections.singletonList(row);
  }

  @Override
  public List<Row> finalize(ExecutorContext ctx) {
    long finalSize = aggregationType.equals("average") ? totalSize / Math.max(count, 1) : totalSize;
    long finalTime = aggregationType.equals("average") ? totalTime / Math.max(count, 1) : totalTime;

    double convertedSize = convertSize(finalSize, sizeUnit);
    double convertedTime = convertTime(finalTime, timeUnit);

    Row result = new Row();
    result.add(targetSizeCol, convertedSize);
    result.add(targetTimeCol, convertedTime);

    List<Row> out = new ArrayList<>();
    out.add(result);
    return out;
  }

  private long parseBytes(String sizeStr) {
    sizeStr = sizeStr.trim().toUpperCase();
    if (sizeStr.endsWith("KB")) return (long) (Double.parseDouble(sizeStr.replace("KB", "")) * 1024);
    if (sizeStr.endsWith("MB")) return (long) (Double.parseDouble(sizeStr.replace("MB", "")) * 1024 * 1024);
    if (sizeStr.endsWith("GB")) return (long) (Double.parseDouble(sizeStr.replace("GB", "")) * 1024 * 1024 * 1024);
    if (sizeStr.endsWith("B")) return Long.parseLong(sizeStr.replace("B", ""));
    return Long.parseLong(sizeStr);
  }

  private long parseTime(String timeStr) {
    timeStr = timeStr.trim().toLowerCase();
    if (timeStr.endsWith("ms")) return (long) (Double.parseDouble(timeStr.replace("ms", "")) * 1_000_000);
    if (timeStr.endsWith("s")) return (long) (Double.parseDouble(timeStr.replace("s", "")) * 1_000_000_000);
    if (timeStr.endsWith("min")) return (long) (Double.parseDouble(timeStr.replace("min", "")) * 60_000_000_000L);
    if (timeStr.endsWith("ns")) return Long.parseLong(timeStr.replace("ns", ""));
    return Long.parseLong(timeStr);
  }

  private double convertSize(long size, String unit) {
    switch (unit.toUpperCase()) {
      case "KB": return size / 1024.0;
      case "MB": return size / (1024.0 * 1024);
      case "GB": return size / (1024.0 * 1024 * 1024);
      default: return size; // bytes
    }
  }

  private double convertTime(long time, String unit) {
    switch (unit.toLowerCase()) {
      case "ms": return time / 1_000_000.0;
      case "s": return time / 1_000_000_000.0;
      case "min": return time / 60_000_000_000.0;
      default: return time; // ns
    }
  }
}

