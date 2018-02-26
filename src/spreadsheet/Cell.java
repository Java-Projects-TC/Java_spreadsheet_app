package spreadsheet;

import common.api.CellLocation;
import common.api.value.Value;

public class Cell {

  private String expression;
  private Value value;
  private final CellLocation location;
  private Spreadsheet spreadsheet;

  Cell(CellLocation location, Spreadsheet spreadsheet){
    this("", location, spreadsheet);
  }

  Cell(String expression, CellLocation location, Spreadsheet spreadsheet){
    this.expression = expression;
    this.value = null;
    this.location = location;
    this.spreadsheet = spreadsheet;
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
}
