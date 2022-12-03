package fr.ubx.poo.ubomb.go;

import fr.ubx.poo.ubomb.go.decor.bonus.*;

// Double dispatch visitor pattern
public interface TakeVisitor {
    // Key
    default void take(Key key) {}
    // Heart
    default void take(Heart heart) {}
    // Bomb capacity
    default void take(BombCapacity bombCapacity) {}
    // Bomb range
    default void take(BombRange bombRange) {}
}
