/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;

// For GameObjects that can move
public interface Movable {
    default boolean canMove(Direction direction) { return false; }
    default boolean canMove(Direction direction, Game game) { return false; }
    void doMove(Direction direction);
}
