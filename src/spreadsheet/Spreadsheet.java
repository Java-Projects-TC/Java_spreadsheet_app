package spreadsheet;

import common.api.CellLocation;
import common.api.Tabular;
import common.api.value.StringValue;
import common.api.value.Value;
import java.util.HashMap;
import java.util.Map;

public class Spreadsheet implements Tabular {

  private Map<CellLocation, Cell> cells = new HashMap<>();

  @Override
  public void setExpression(CellLocation location, String expression) {
    if (!cells.containsKey(location)) {
      cells.put(location, new Cell(expression, location, this));
    }
    cells.get(location).setExpression(expression);
    cells.get(location).setValue(new StringValue(expression));
  }

  @Override
  public String getExpression(CellLocation location) {
      return cells.getOrDefault(location, new Cell(location, this))
          .getExpression();
  }

  @Override
  public Value getValue(CellLocation location) {
    return cells.getOrDefault(location, new Cell(location, this)).getValue();
  }

  @Override
  public void recompute() {

  }
}
