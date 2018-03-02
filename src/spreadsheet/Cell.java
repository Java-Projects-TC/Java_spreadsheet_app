package spreadsheet;

import common.api.CellLocation;
import common.api.ExpressionUtils;
import common.api.monitor.Tracker;
import common.api.value.InvalidValue;
import common.api.value.Value;
import java.util.HashSet;
import java.util.Set;

public class Cell implements Tracker<Cell>{

  private String expression;
  private Value value;
  private final CellLocation location;
  private Spreadsheet spreadsheet;
  private Set<Cell> trackedCells = new HashSet<>();
  private Set<Tracker<Cell>> trackedBy = new HashSet<>();

  Cell(CellLocation location, Spreadsheet spreadsheet){
    this.location = location;
    this.spreadsheet = spreadsheet;
  }

  Set<Cell> getTracked() {
    return trackedCells;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    // remove all this from cells that track it since its expression changes
    for (Cell c : trackedCells) {
      c.removeTracker(this);
    }

    // set expression to new one...
    this.expression = expression;

    // ...and value to an Invalid one
    setValue(new InvalidValue(expression));

    // check if recomputing is required, if so, do it
    checkAndRecompute();

    // extract cell locations and convert to cells
    // STREAMS HERE????
    for (CellLocation location : ExpressionUtils.
        getReferencedLocations(expression)) {
      Cell cellInExp = spreadsheet.getCell(location);
      cellInExp.AddTracker(this);
      trackedCells.add(cellInExp);
    }
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

    // tell spreadsheet needs recomputing
    if (checkAndRecompute()) {

      // change value to invalid value
      this.setValue(new InvalidValue(expression));

      // Inform all Tracker<Cell> subscribed that expression has changed
      for (Tracker<Cell> cell : trackedBy) {
        cell.update(this);
      }
    }
  }

  private boolean checkAndRecompute() {
      return spreadsheet.getCellsToCompute().add(this);
  }

  private void AddTracker(Tracker<Cell> tracker) {
    trackedBy.add(tracker);
  }

  private void removeTracker(Tracker<Cell> tracker) {
    trackedBy.remove(tracker);
  }


}
