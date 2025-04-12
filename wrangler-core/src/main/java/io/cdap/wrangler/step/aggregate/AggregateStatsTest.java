package io.cdap.wrangler.steps.aggregate;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.parser.TextArguments;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class AggregateStatsTest {

  @Test
  public void testAggregateTotalMBSeconds() throws Exception {
    List<Row> rows = Arrays.asList(
      new Row("size", "10MB").add("duration", "2s"),
      new Row("size", "20MB").add("duration", "3s")
    );

    AggregateStats directive = new AggregateStats();
    directive.initialize(new TextArguments(Arrays.asList(
      "size", "duration", "totalSize", "totalTime", "MB", "seconds"
    )));

    ExecutorContext context = new MockExecutorContext(rows.size());
    List<Row> result = directive.execute(rows, context);

    assertEquals(1, result.size());
    Row finalRow = result.get(0);
    assertEquals(30.0, finalRow.getValue("totalSize"));
    assertEquals(5.0, finalRow.getValue("totalTime"));
  }

  @Test
  public void testAggregateAverageMode() throws Exception {
    List<Row> rows = Arrays.asList(
      new Row("size", "15MB").add("duration", "3s"),
      new Row("size", "15MB").add("duration", "3s")
    );

    AggregateStats directive = new AggregateStats();
    directive.initialize(new TextArguments(Arrays.asList(
      "size", "duration", "avgSize", "avgTime", "MB", "seconds", "average"
    )));

    ExecutorContext context = new MockExecutorContext(rows.size());
    List<Row> result = directive.execute(rows, context);

    Row finalRow = result.get(0);
    assertEquals(15.0, finalRow.getValue("avgSize"));
    assertEquals(3.0, finalRow.getValue("avgTime"));
  }
}
