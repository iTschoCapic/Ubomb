package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class BombCapacity extends Bonus {

    private boolean isPositive;

    public BombCapacity(Position position, boolean isPositive) {
        super(position);
        this.isPositive = isPositive;
    }

    public boolean positive() {
        return isPositive;
    }

    @Override
    public void explode() {}

    @Override
    public void takenBy(Player player) {
        player.take(this);
        this.remove();
    }
}
