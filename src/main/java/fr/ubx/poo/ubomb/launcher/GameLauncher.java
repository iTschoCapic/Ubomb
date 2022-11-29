package fr.ubx.poo.ubomb.launcher;

import fr.ubx.poo.ubomb.game.Configuration;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Level;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.launcher.MapRepoStringRLE;
import fr.ubx.poo.ubomb.game.Configuration;
/*To complete if we have time // Export as file import java.io.Writer;
import java.io.IOException;
import java.io.*;*/

public class GameLauncher {
    // To complete if we have time // Export as file  private static MapRepoStringRLE mapRepoStringRLE = new MapRepoStringRLE();

    public static Game loadDefault() {
        Configuration configuration = new Configuration(new Position(0, 0), 3, 5, 4000, 5, 1000);
        return new Game(configuration, new Level(new MapLevelDefault()));
    }

    public static Game load(Configuration configuration, MapLevel mapLevel) {
        return new Game(configuration, new Level(mapLevel));
    }

    /*To complete if we have time // Export as file
    public static void export(Writer ou, Configuration configuration, MapLevel mapLevel){
        try{
            ou.write("compression = "+configuration.toString());
            ou.write(mapRepoStringRLE.export(mapLevel));
            ou.flush();
        } catch(IOException IOex){
                        return;
                    }
        return;
    }*/

}
