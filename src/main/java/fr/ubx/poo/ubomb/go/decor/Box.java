package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.character.Monster;
import fr.ubx.poo.ubomb.go.decor.bonus.*;

public class Box extends Decor implements Movable {

    public Box(Position position) {
        super(position);
    }

    @Override
    public boolean canMove(Direction direction, Game game) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.grid().get(nextPos);
        if (game.grid().inside(nextPos)) {
            if (next != null) {
                
                return next.walkableBy(this);
            } else {
                for (Monster monster : game.getMonsters()){
                    if (monster.getPosition().equals(nextPos)){
                        return monster.walkableBy(this);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    @Override
    public boolean walkableBy(Player player) {
        return canMove(player.getDirection(), player.game);
    }
}
