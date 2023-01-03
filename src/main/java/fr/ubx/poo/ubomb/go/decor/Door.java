package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.bonus.Bomb;

public class Door extends Decor {

    private boolean isOpen;
    private boolean isSuperior;

    public Door(Position position, boolean isOpen, boolean isSuperior) {
        super(position);
        this.isOpen = isOpen;
        this.isSuperior = isSuperior;
    }

    public void open(Game game) {
        isOpen = true;
        game.grid().setDoor(this.getPosition(), this.isSuperior());
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isSuperior() {
        return isSuperior;
    }

    @Override
    public void takenBy(Player player) {
        if (isOpen) {
            if (isSuperior) {
                // Go to superior level
                if (player.game.getMaxLevel() == player.game.getCurrentLevel()+1){
                    return;
                }
                player.game.setCurrentLevel(1);
                player.game.update(-1);
            }
            else {
                // Go to inferior level
                if (player.game.getCurrentLevel() == 0){
                    return;
                }
                player.game.setCurrentLevel(-1);
                player.game.update(1);
            }
            player.setDirection(Direction.DOWN);
            player.teleport(player.game.grid().getDoor(isSuperior));
        }
    }

    @Override
    public boolean walkableBy(Player player) {
        return isOpen();
    }
}
