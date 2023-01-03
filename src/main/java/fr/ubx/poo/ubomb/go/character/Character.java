package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.TakeVisitor;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.go.decor.*;

public abstract class Character extends GameObject {

    protected Direction direction;
    protected int lives;
    public Game game;
    protected boolean moveRequested = false;

    public Character(Game game, Position position) {
        super(game, position);
        this.game = game;
    }

    public Character(Position position) {
        super(position);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setLives(int delta){
        this.lives = delta;
    }

    public int getLives() {
        return lives;
    }

    public void updateLives(int delta){
        this.lives+=delta;
    }

    

    @Override
    public boolean walkableBy(Box box) { return false; }

}
