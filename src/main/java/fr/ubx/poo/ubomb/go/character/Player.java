/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.TakeVisitor;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.go.decor.*;

public class Player extends Character implements Movable, TakeVisitor {

    private long invincibilityTime;
    private boolean isInvincible = false;
    private int keys;
    private int availableBombs;
    private int bombBagCapacity;
    private int bombRange;

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = game.configuration().playerLives();
        this.invincibilityTime = game.configuration().playerInvincibilityTime();
        this.bombBagCapacity = game.configuration().bombBagCapacity();
        this.bombRange = 1;
        this.availableBombs = bombBagCapacity;
    }

    public void doMove(Direction direction) {
    // This method is called only if the move is possible, do not check again
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.grid().get(nextPos);
        if (next != null) {
            next.takenBy(this);
        }
        if (!(next instanceof Door)){
            setPosition(nextPos);
        }
    }

    public void addAvailableBombs() {
        if (availableBombs < bombBagCapacity){
            this.availableBombs++;
        }
    }

    public int getAvailableBombs() {
        return availableBombs;
    }

    public int getBombRange() {
        return bombRange;
    }

    public int getKeys() {
        return keys;
    }

    public void teleport(Position position){
        setPosition(position);
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.grid().get(nextPos);
        if (game.grid().inside(nextPos)) {
            if (next != null) {
                return next.walkableBy(this);
            }
            return true;
        }
        return false;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }

    public void useKey() {
        Position nextPos = this.getDirection().nextPosition(getPosition());
        GameObject next = game.grid().get(nextPos);
        if (next instanceof Door) {
            if (!((Door)next).isOpen() && getKeys() > 0) {
                keys--;
                ((Door)next).open(this.game);
                ((Door)next).setModified(true);
            }
        }
    }

    public void useBomb(){
        Position myPosition = this.getPosition();
        GameObject here = game.grid().get(myPosition);
        if (this.availableBombs > 0){
            if (here == null) {
                for (Monster monster : game.getMonsters()){
                    if (monster.getPosition().equals(myPosition)){
                        return;
                    }
                    this.availableBombs--;
                }
            }
        }
    }

    //takes implementations

    @Override
    public void take(Key key) {
        this.keys++;
    }

    @Override
    public void take(Heart heart) {
        updateLives(1);
    }

    @Override
    public void take(BombCapacity bombCapacity) {
        if (bombCapacity.positive()) {
            this.bombBagCapacity++;
        }
        else {
            if (this.bombBagCapacity > 1) {
                this.bombBagCapacity--;
                if (this.availableBombs > this.bombBagCapacity)
                    this.availableBombs = this.bombBagCapacity;
            }
        }
    }

    @Override
    public void take(BombRange bombRange) {
        if (bombRange.positive()) {
            this.bombRange++;
        }
        else {
            if (this.bombRange > 1) {
                this.bombRange--;
            }
        }
    }

}
