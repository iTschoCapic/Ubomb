package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.engine.Timer;
import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.game.AStar;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.TakeVisitor;
import fr.ubx.poo.ubomb.go.Takeable;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.engine.Timer;

import java.util.List;
import java.util.ArrayList;

public class Monster extends Character {

    private long lastMove = 0;
    private long velocity = (long)1e10;
    public Game game = null;
    private boolean isRandom = true;
    private List<AStar.Node> path = new ArrayList<>();
    private AStar astar;
    private AStar.Node start;
    private AStar.Node end;
    private Timer timer;

    public Monster(Position position) {
        super(position);
        this.direction = Direction.DOWN;
        this.lives = 1;
    }

    @Override
    public boolean walkableBy(Monster monster) {
        return false;
    }

    public final boolean canMove(Direction direction) {
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

    public void doMove(Direction direction){
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.grid().get(nextPos);
        setPosition(nextPos);
    }

    public void update(long now) {
        if (moveRequested == true){
            if ((now - lastMove) > this.velocity) {
                lastMove = now;
                if (isRandom){
                    this.direction = this.direction.random();
                    setModified(true);
                } else {
                    if (this.game != null){
                        this.astar = new AStar(game, this);
                        this.start = astar.getNodeAt(astar.nodes, this.getPosition().getX(), this.getPosition().getY());
                        this.end = astar.getNodeAt(astar.nodes, this.game.player().getPosition().getX(), this.game.player().getPosition().getY());
                        if (this.start.getPosition() != this.end.getPosition()){
                            this.path = this.astar.findPath(this.start, this.end);
                            if (this.path == null){
                                this.direction = this.direction.random();
                                setModified(true);
                            } else {
                                if (path.size() > 1){
                                    if (path.get(1).getX() > this.getPosition().getX()){
                                        this.direction = Direction.RIGHT;
                                    } else if (path.get(1).getX() < this.getPosition().getX()){
                                        this.direction = Direction.LEFT;
                                    } else if (path.get(1).getY() > this.getPosition().getY()){
                                        this.direction = Direction.DOWN;
                                    } else if (path.get(1).getY() < this.getPosition().getY()){
                                        this.direction = Direction.UP;
                                    }
                                    setModified(true);
                                }
                            }
                        }
                    }
                }
                if (canMove(this.direction) && isModified()) {
                    doMove(this.direction);
                }
            }
        }
    }

    public void setTimer(long time){
        this.timer = new Timer(time);
    }

    public Timer getTimer(){
        return this.timer;
    }

    public void setRandom(boolean isRandom){
        this.isRandom = isRandom;
    }

    public void setMonsterVelocity(long velocity){
        this.velocity = (long)1e10 / velocity;
    }

    public void setGame(Game game){
        this.game = game;
    }

    public void setMoveRequested(boolean status){
        this.moveRequested = status;
    }

    public boolean getMoveRequested(){
        return this.moveRequested;
    }

}
