package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class Door extends Decor {

    private boolean isOpen;
    private boolean isSuperior;

    public Door(Position position, boolean isOpen, boolean isSuperior) {
        super(position);
        this.isOpen = isOpen;
        this.isSuperior = isSuperior;
    }

    public void open() {
        isOpen = true;
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
            
        }
    }

    @Override
    public boolean walkableBy(Player player) {
        return isOpen();
    }
}
