package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;

public class Box extends Decor implements Movable {
    public Box(Position position) {
        super(position);
    }

    @Override
    public boolean canMove(Direction direction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void doMove(Direction direction) {
        // TODO Auto-generated method stub
        
    }
}
