package spreadsheet;

import common.api.CellLocation;
import common.api.Tabular;
import common.api.value.StringValue;
import common.api.value.Value;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;

public class Spreadsheet implements Tabular {

  private Map<CellLocation, Cell> cells = new HashMap<>();
  private Queue<Cell> cellsToCompute = new ArrayDeque<>();

  Cell getCell(CellLocation location) {
    Cell cell = cells.get(location);
    if (cell == null) {
      cell = new Cell(location, this);
      cells.put(location, cell);
    }
    return cell;
  }

  Queue<Cell> getCellsToCompute() { return cellsToCompute;
  }

  @Override
  public void setExpression(CellLocation location, String expression) {
    if (!cells.containsKey(location)) {
      cells.put(location, new Cell(location, this));
    }
      cells.get(location).setExpression(expression);
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
      Cell c = cellsToCompute.poll();
      c.setValue(new StringValue(c.getExpression()));
    }
  }

  private void recomputeCell(Cell c) {
    // set cell to string
    c.setValue(new StringValue(c.getExpression()));
    // check for loops
    checkLoops(c, new LinkedHashSet<>());
    // something
  }

  private void checkLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
    if (cellsSeen.contains(c)) {
      markAsValidatedLoop(c, cellsSeen);
    } else {
      cellsSeen.add(c);
      for (Cell cellAfterC : c.getTracked()) {
        checkLoops(cellAfterC, cellsSeen);
      }
      cellsSeen.remove(c);
    }
  }

  private void markAsValidatedLoop(Cell startCell, LinkedHashSet<Cell> cells) {

  }

}
