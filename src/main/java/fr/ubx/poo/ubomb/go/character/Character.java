package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.go.decor.*;

public abstract class Character extends GameObject {

    public Character(Game game, Position position) {
        super(game, position);
    }

    public Character(Position position) {
        super(position);
    }

    @Override
    public boolean walkableBy(Box box) { return false; }

}
