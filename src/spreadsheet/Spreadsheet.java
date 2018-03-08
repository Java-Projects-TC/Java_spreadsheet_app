package spreadsheet;

import common.api.CellLocation;
import common.api.ExpressionUtils;
import common.api.Tabular;
import common.api.value.InvalidValue;
import common.api.value.LoopValue;
import common.api.value.StringValue;
import common.api.value.Value;
import common.api.value.ValueEvaluator;
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

  void addToCellsToCompute(Cell c) {
    cellsToCompute.add(c);
  }

  boolean needsRecomputing(Cell c) {
    return !cellsToCompute.contains(c);
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
    // can't use for loop here as we change the size of cellsToCompute.
    while (!cellsToCompute.isEmpty()) {
      Cell cell = cellsToCompute.iterator().next();
      recomputeCell(cell);
    }
  }

  private void recomputeCell(Cell c) {
    // set cell to string
    c.setValue(new StringValue(c.getExpression()));
    // check for loops and assign values
    checkLoops(c, new LinkedHashSet<>());
    // check if cell c is not part of (or depends upon) a loop
    if (c.getValue() instanceof StringValue) {
      setAndCalculateCellValue(c);
    }
    cellsToCompute.remove(c);
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
    boolean hasReachedStartNode = false;
    for (Cell cell : cells) {
      cellsToCompute.remove(cell);
      if (cell.equals(startCell)) {
        hasReachedStartNode = true;
      }
      cell.setValue(hasReachedStartNode ? LoopValue.INSTANCE :
          new InvalidValue(cell.getExpression()));
    }
  }

  // found anonymous inner class confusing, please explain why the spec
  // requires a queue in our ppt? I didn't understand their implementation.
  private Map<CellLocation, Double> buildMapOfTrackedCells(Cell c) {
    Map<CellLocation, Double> trackedCellMap = new HashMap<>();
    for (Cell trackedCell : c.getTracked()) {
      if (cellsToCompute.contains(trackedCell)) {
        setAndCalculateCellValue(trackedCell);
        cellsToCompute.remove(trackedCellMap);
      }
      trackedCell.getValue().evaluate(new ValueEvaluator() {
        @Override
        public void evaluateDouble(double value) {
          trackedCellMap.put(trackedCell.getLocation(), value);
        }

        @Override
        public void evaluateLoop() {

        }

        @Override
        public void evaluateString(String expression) {

        }

        @Override
        public void evaluateInvalid(String expression) {

        }
      });
    }
    return trackedCellMap;
  }

  private void  setAndCalculateCellValue(Cell c) {
    c.setValue(ExpressionUtils.computeValue(c.getExpression(),
        buildMapOfTrackedCells(c)));
  }
}
