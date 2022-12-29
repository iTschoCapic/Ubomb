package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.game.Direction;

public record Position (int x, int y) {

    public Position(Position position) {
        this(position.x, position.y);
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public Direction Compaire(Position position, boolean alreadyTried){
        if (alreadyTried){
            if (this.x > position.x){
                return Direction.LEFT;
                
            } else if (this.x < position.x){
                return Direction.RIGHT;
            } else {
                if (this.y > position.y){
                    return Direction.UP;
                } else {
                    return Direction.DOWN;
                }
            }
        } else {
            if (this.y > position.y){
                return Direction.UP;
            } else if (this.y < position.y) {
                return Direction.DOWN;
            } else {
                if (this.x > position.x){
                    return Direction.LEFT;
                } else {
                    return Direction.RIGHT;
                }
            }
        }
        
    }
}
