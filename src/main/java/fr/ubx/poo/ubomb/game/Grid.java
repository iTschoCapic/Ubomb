package fr.ubx.poo.ubomb.game;


import fr.ubx.poo.ubomb.go.decor.Decor;
import fr.ubx.poo.ubomb.go.decor.bonus.Bomb;
import fr.ubx.poo.ubomb.go.character.*;

import java.util.*;

public interface Grid {
    int width();
    int height();

    Decor get(Position position);

    ArrayList<Monster> getMonsters();

    ArrayList<Bomb> getBombs();

    boolean getPrincess();

    Position getDoor(boolean isSuperior);

    void setDoor(Position position, boolean isSuperior);

    void remove(Position position);

    Collection<Decor> values();

    boolean inside(Position nextPos);

    void set(Position position, Decor decor);
}
