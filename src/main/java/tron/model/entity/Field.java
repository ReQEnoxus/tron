package tron.model.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

public class Field {
    private CellType[][] field;
    private ArrayList<ArrayList<ObjectProperty<Paint>>> paintProperties;

    public Field(int rows, int cols) {
        field = new CellType[rows][cols];
        paintProperties = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            paintProperties.add(new ArrayList<>());
            for (int col = 0; col < cols; col++) {
                field[row][col] = CellType.EMPTY;
                paintProperties.get(row).add(new SimpleObjectProperty<>(cellColor(CellType.EMPTY)));
            }
        }
    }

    public CellType[][] getField() {
        return field;
    }

    public void setField(CellType[][] field) {
        this.field = field;
    }

    public void setCell(int row, int col, CellType type) {
        field[row][col] = type;
        paintProperties.get(row).get(col).setValue(cellColor(type));
    }

    private Paint cellColor(CellType type) {
        switch (type) {
            case EMPTY:
                return Color.color(.14, .14, .14);
            case PLAYER1:
                return Color.valueOf("#ff2424");
            case PLAYER2:
                return Color.valueOf("#55ff42");
            case PLAYER3:
                return Color.valueOf("#42a4ff");
            case PLAYER4:
                return Color.valueOf("#ffef42");
            default:
                return null;
        }
    }

    public ArrayList<ArrayList<ObjectProperty<Paint>>> getPaintProperties() {
        return paintProperties;
    }

    public void setPaintProperties(ArrayList<ArrayList<ObjectProperty<Paint>>> paintProperties) {
        this.paintProperties = paintProperties;
    }

    public void reset(int rows, int cols) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                field[row][col] = CellType.EMPTY;
                paintProperties.get(row).get(col).setValue(cellColor(CellType.EMPTY));
            }
        }
    }

    public void reset() { // default values
        for (int row = 0; row < 128; row++) {
            for (int col = 0; col < 60; col++) {
                field[row][col] = CellType.EMPTY;
                paintProperties.get(row).get(col).setValue(cellColor(CellType.EMPTY));
            }
        }
    }
}
