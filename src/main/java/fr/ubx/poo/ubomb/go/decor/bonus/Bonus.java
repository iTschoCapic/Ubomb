/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.*;
import fr.ubx.poo.ubomb.go.decor.Decor;

public abstract class Bonus extends Decor {
    public Bonus(Position position) {
        super(position);
    }

    @Override
    public boolean walkableBy(Player player) {
        return true;
    }

    @Override
    public boolean walkableBy(Monster monster) {
        return true;
    }

    @Override
    public void explode() {
        remove();
    }

}
