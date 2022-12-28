package fr.ubx.poo.ubomb.launcher;

import fr.ubx.poo.ubomb.game.Configuration;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Level;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.launcher.MapRepoStringRLE;
import fr.ubx.poo.ubomb.game.Configuration;

public class GameLauncher {

    public static Game loadDefault() {
        Configuration configuration = new Configuration(new Position(0, 0), 3, 5, 4000, 5, 1000);
        return new Game(configuration, new Level(new MapLevelDefault()));
    }

    public static Game load(Configuration configuration, MapLevel mapLevel) {
        return new Game(configuration, new Level(mapLevel));
    }

}
