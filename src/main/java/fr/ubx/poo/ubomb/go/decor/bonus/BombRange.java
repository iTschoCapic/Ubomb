package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.character.Monster;

public class BombRange extends Bonus {

    private boolean isPositive;

    public BombRange(Position position, boolean isPositive) {
        super(position);
        this.isPositive = isPositive;
    }

    public boolean positive() {
        return isPositive;
    }

    @Override
    public void explode() {}

    @Override
    public boolean walkableBy(Monster monster) {
        return false;
    }

    @Override
    public void takenBy(Player player) {
        player.take(this);
        this.remove();
    }
}