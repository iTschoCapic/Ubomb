package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.engine.Timer;
import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.TakeVisitor;
import fr.ubx.poo.ubomb.go.decor.bonus.*;

public class Princess extends GameObject {

    private Direction direction;

    public Princess(Position position) {
        super(position);
        this.direction = Direction.DOWN;
    }

    @Override
    public boolean walkableBy(Player player) {
        return true;
    }
}
