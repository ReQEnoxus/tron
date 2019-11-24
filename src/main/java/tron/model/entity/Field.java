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

//        System.out.println("row = " + row);
//        System.out.println("col = " + col);
//        System.out.println("type = " + type);
    }

    private Paint cellColor(CellType type) {
        switch (type) {
            case EMPTY:
                return Color.color(.2, .2, .2);
            case PLAYER1:
                return Color.RED;
            case PLAYER2:
                return Color.GREEN;
            case PLAYER3:
                return Color.BLUE;
            case PLAYER4:
                return Color.YELLOW;
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
