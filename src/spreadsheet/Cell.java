package spreadsheet;

import common.api.CellLocation;
import common.api.ExpressionUtils;
import common.api.monitor.Tracker;
import common.api.value.Value;
import java.util.HashSet;
import java.util.Set;

public class Cell implements Tracker<Cell>{

  private String expression;
  private Value value;
  private final CellLocation location;
  private Spreadsheet spreadsheet;
  private Set<Tracker<Cell>> trackedCells;
  private Set<Tracker<Cell>> trackedBy;

  Cell(CellLocation location, Spreadsheet spreadsheet){
    this("", location, spreadsheet);
  }

  Cell(String expression, CellLocation location, Spreadsheet spreadsheet){
    this.expression = expression;
    this.value = null;
    this.location = location;
    this.spreadsheet = spreadsheet;
    this.trackedCells = new HashSet<>();

  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public Value getValue() {
    return value;
  }

  public void setValue(Value value) {
    this.value = value;
  }

  public CellLocation getLocation() {
    return location;
  }

  @Override
  public void update(Cell changed) {

    // remove cells it was depending upon from tracker
    trackedCells.forEach(t -> changed.removeTracker(t));

    // change cells expression to new one
    this.setExpression(changed.getExpression());

    // extract cell locations and convert to cells

    // NOTE: can you use streams here? attempt later.
    Set<CellLocation> refCellLocations = ExpressionUtils.getReferencedLocations
        (changed.getExpression());
    for (CellLocation refCellLocation : refCellLocations) {
      trackedCells.add(spreadsheet.getCell(refCellLocation));
    }

    // Inform all Tracker<Cell> subscribed that expression has changed
    for (Tracker<Cell> tracker : trackedBy) {
      tracker.update(this);
      spreadsheet.addToCellsToCompute((Cell) tracker); // Had to cast here??
    }
  }

  private void removeTracker(Tracker<Cell> tracker) {
    trackedCells.remove(tracker);
  }
}
