package tron.model.helpers;

import tron.model.entity.Direction;
import tron.model.entity.Player;
import tron.model.entity.Point;

public class PlayerHelper {
    public static Point getInitialPoint(int id) {
        switch (id) {
            case 1:
                return new Point(40, 15);
            case 2:
                return new Point(88, 15);
            case 3:
                return new Point(40, 40);
            case 4:
                return new Point(88, 40);
            default:
                throw new IllegalArgumentException("Game does not support more than 4 players");
        }
    }

    public static Direction getInitialDirection(Player player) {
        switch (player.getPlayerNumber()) {
            case 1:
                return Direction.LEFT;
            case 2:
                return Direction.RIGHT;
            case 3:
                return Direction.LEFT;
            case 4:
                return Direction.RIGHT;
            default:
                throw new IllegalArgumentException("Game does not support more than 4 players");
        }
    }

}
