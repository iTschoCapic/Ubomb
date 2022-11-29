package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class Heart extends Bonus {
    public Heart(Position position) {
        super(position);
    }

    @Override
    public boolean walkableBy(Player player) {
        return true;
    }

    @Override
    public void takenBy(Player player) {
        player.updateLives(1);
        this.remove();
    }

    @Override
    public void explode() {}

}
