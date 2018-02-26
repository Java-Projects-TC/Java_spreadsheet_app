package spreadsheet;

import common.gui.SpreadsheetGUI;

public class Main {

  private static final int DEFAULT_NUM_ROWS = 5000;
  private static final int DEFAULT_NUM_COLUMNS = 5000;

  public static void main(String[] args) {
    Spreadsheet spreadsheet = new Spreadsheet();
    SpreadsheetGUI gui;
    if (args.length == 2) {
      gui = new SpreadsheetGUI(spreadsheet, Integer.parseInt
          (args[0]), Integer.parseInt(args[1]));
    } else {
      gui = new SpreadsheetGUI(spreadsheet, DEFAULT_NUM_ROWS,
          DEFAULT_NUM_COLUMNS);
    }
    gui.start();
  }

}
