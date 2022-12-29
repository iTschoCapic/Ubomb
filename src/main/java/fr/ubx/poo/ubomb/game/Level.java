package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.character.*;
import fr.ubx.poo.ubomb.launcher.Entity;
import fr.ubx.poo.ubomb.launcher.MapLevel;

import java.util.*;

public class Level implements Grid {

    private final int width;

    private final int height;

    private final MapLevel entities;

    private final ArrayList<Monster> monsters = new ArrayList<>();

    private final Map<Position, Decor> elements = new HashMap<>();

    private boolean princess = false;

    public Level(MapLevel entities) {
        this.entities = entities;
        this.width = entities.width();
        this.height = entities.height();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(i, j);
                Entity entity = entities.get(i, j);
                switch (entity) {
                    case Stone:
                        elements.put(position, new Stone(position));
                        break;
                    case Tree:
                        elements.put(position, new Tree(position));
                        break;
                    case Key:
                        elements.put(position, new Key(position));
                        break;
                    case Box:
                        elements.put(position, new Box(position));
                        break;
                    case Heart:
                        elements.put(position, new Heart(position));
                        break;
                    case Princess:
                        elements.put(position, new Princess(position));
                        princess = true;
                        break;
                    case DoorPrevOpened:
                        elements.put(position, new Door(position, true, false));
                        break;
                    case DoorNextOpened:
                        elements.put(position, new Door(position, true, true));
                        break;
                    case DoorNextClosed:
                        elements.put(position, new Door(position, false, true));
                        break;
                    case BombNumberInc:
                        elements.put(position, new BombCapacity(position, true));
                        break;
                    case BombNumberDec:
                        elements.put(position, new BombCapacity(position, false));
                        break;
                    case BombRangeInc:
                        elements.put(position, new BombRange(position, true));
                        break;
                    case BombRangeDec:
                        elements.put(position, new BombRange(position, false));
                        break;
                    case Monster:
                        monsters.add(new Monster(position));
                        break;
                    case Empty: break;
                    default:
                        throw new RuntimeException("EntityCode " + entity.name() + " not processed");
                }
            }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return elements.get(position);
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public boolean getPrincess() {
        return princess;
    }

    @Override
    public void remove(Position position) {
        elements.remove(position);
    }

    public Collection<Decor> values() {
        return elements.values();
    }


    @Override
    public boolean inside(Position position) {
        return position.x() >= 0 && position.y() >= 0 && position.x() < this.width && position.y() < this.height;
    }

    @Override
    public void set(Position position, Decor decor) {
        if (!inside(position))
            throw new IllegalArgumentException("Illegal Position");
        if (decor != null)
            elements.put(position, decor);
    }


}
