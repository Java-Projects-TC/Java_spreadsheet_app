package spreadsheet;

import common.api.CellLocation;
import common.api.Tabular;
import common.api.value.StringValue;
import common.api.value.Value;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Spreadsheet implements Tabular {

  private Map<CellLocation, Cell> cells = new HashMap<>();
  private Queue<Cell> cellsToCompute = new ArrayDeque<>();

  Cell getCell(CellLocation location) {
    return cells.getOrDefault(location, new Cell(location, this));
  }

  void addToCellsToCompute(Cell cell) {
    cellsToCompute.add(cell);
  }

  @Override
  public void setExpression(CellLocation location, String expression) {
    if (!cells.containsKey(location)) {
      cells.put(location, new Cell(expression, location, this));
    } else {
      cells.get(location).setExpression(expression);
    }
    cells.get(location).setValue(new StringValue(expression));
  }

  @Override
  public String getExpression(CellLocation location) {
    return cells.containsKey(location) ? cells.get(location).getExpression()
        : "";
  }

  @Override
  public Value getValue(CellLocation location) {
    return cells.containsKey(location) ? cells.get(location).getValue() : null;
  }

  @Override
  public void recompute() {
    for (int i = 0; i < cellsToCompute.size(); i++) {
      Cell cell = cellsToCompute.poll();
      cell.setValue(new StringValue(cell.getExpression()));
    }
  }

}
