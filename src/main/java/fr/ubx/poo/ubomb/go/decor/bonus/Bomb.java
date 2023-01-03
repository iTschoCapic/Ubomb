package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Monster;
import fr.ubx.poo.ubomb.engine.Timer;

public class Bomb extends Bonus {

    private Timer timer;
    private boolean isInWorld = true;

    public Bomb(Position position) {
        super(position);
        this.timer = new Timer(4000);
    }

    public Timer getTimer(){
        return this.timer;
    }

    public int getDigit(){
        if (this.timer.remaining() <= 4000 && this.timer.remaining() > 3000){
            return 3;
        } else if (this.timer.remaining() < 3000 && this.timer.remaining() > 2000){
            return 2;
        } else if (this.timer.remaining() < 2000 && this.timer.remaining() > 1000){
            return 1;
        } else if (this.timer.remaining() < 1000 && this.timer.remaining() >= -11){
            return 0;
        }
        return -1;
    }

    public void isInWorld(boolean isInWorld){
        this.isInWorld = isInWorld;
    }

    public boolean getIsInWorld(){
        return this.isInWorld;
    }

    @Override
    public void explode() {}

}
