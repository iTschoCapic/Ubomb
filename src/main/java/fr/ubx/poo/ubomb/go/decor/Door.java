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
        isOpen = false;
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
            }
            else {
                // Go to inferior level
            }
        }
    }

    @Override
    public boolean walkableBy(Player player) {
        return isOpen();
    }
}
