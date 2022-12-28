package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Monster;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.launcher.MapLevel;
import fr.ubx.poo.ubomb.launcher.MapRepoStringRLE;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.game.Position;

import java.util.LinkedList;
import java.util.List;
import java.util.*;

public class Game {

    private final Configuration configuration;
    private final Player player;
    private ArrayList<Monster> monsters;
    private Grid grid;
    private int currentLevel;
    private int maxLevel;
    private String[] worldString;
    private final MapRepoStringRLE mapRepoStringRLE = new MapRepoStringRLE();
    private Grid[] loadedGrid = new Grid[100];
    private boolean compression;
    private Position[][] doors;
    private long monsterVelocity;

    public Game(Configuration configuration, Grid grid) {
        this.configuration = configuration;
        this.grid = grid;
        this.player = new Player(this, configuration.playerPosition());
        this.monsters = grid.getMonsters();
        this.currentLevel = 0;
        this.monsterVelocity = configuration.monsterVelocity();
    }

    public Configuration configuration() {
        return configuration;
    }

    // Returns the player, monsters and bomb at a given position
    public List<GameObject> getGameObjects(Position position) {
        List<GameObject> gos = new LinkedList<>();
        if (player().getPosition().equals(position))
            gos.add(player);
        for (Monster monster : getMonsters()) {
            if (monster.getPosition().equals(position))
                monster.setMonsterVelocity(configuration.monsterVelocity());
                gos.add(monster);
            }
        return gos;
    }

    public Grid grid() {
        return grid;
    }

    public void update(int delta){
        if (this.loadedGrid[(getCurrentLevel()+delta)] == null){
            this.loadedGrid[(getCurrentLevel()+delta)] = new Level(new MapLevel(0, 0));
        }
        this.loadedGrid[(getCurrentLevel()+delta)] = this.grid;

        if (this.loadedGrid[getCurrentLevel()] == null){
            if (getCompression() == true){
                this.loadedGrid[getCurrentLevel()] = new Level(mapRepoStringRLE.load(getWorldString(getCurrentLevel())));
            } else {
                this.loadedGrid[getCurrentLevel()] = new Level(mapRepoStringRLE.loadnoc(getWorldString(getCurrentLevel())));
            }
        }
        this.grid = loadedGrid[getCurrentLevel()];
    }

    public Configuration getConfiguration(){
        return this.configuration;
    }

    public Player player() {
        return this.player;
    }

    public void updateMonsters(){
        this.monsters = grid.getMonsters();
    }

    public ArrayList<Monster> getMonsters() {
        updateMonsters();
        return this.monsters;
    }

    public void setCurrentLevel(int delta){
        this.currentLevel += delta;
    }

    public int getCurrentLevel(){
        return this.currentLevel;
    }

    public void setMaxLevel(int delta){
        this.maxLevel = delta;
    }

    public int getMaxLevel(){
        return this.maxLevel;
    }

    public void setWorldString(String[] worlds){
        this.worldString = worlds;
    }

    public String getWorldString(int level){
        return this.worldString[level];
    }

    public void setCompression(boolean compression){
        this.compression = compression;
    }

    public boolean getCompression(){
        return this.compression;
    }

    public void setDoors(int level, int delta, Position position){
        this.doors[level][delta] = position;
    }

    public Position getDoor(int level, int delta){
        return this.doors[level][delta];
    }

    public void setMonsterVelocity(long velocity, int delta, int level){
        for (Monster monster : getMonsters()){
            if (getCurrentLevel() == level+delta){
                monster.setMonsterVelocity(velocity+(delta));
                this.monsterVelocity = velocity+(delta);
            }
        }
    }

    public long getMonsterVelocity(){
        return this.monsterVelocity;
    }

}
