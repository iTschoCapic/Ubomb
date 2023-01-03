package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.engine.GameEngine;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Configuration;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.launcher.GameLauncher;
import fr.ubx.poo.ubomb.launcher.MapLevel;
import fr.ubx.poo.ubomb.launcher.MapRepoStringRLE;

import java.util.Properties;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class GameLauncherView extends BorderPane {
    private MapLevel[] mapLevel = new MapLevel[100]; // Fix to have variable amount of MapLevel
    private Configuration configuration;
    private final FileChooser fileChooser = new FileChooser();

    private int integerProperty(Properties config, String name, int defaultValue) {
        return Integer.parseInt(config.getProperty(name, Integer.toString(defaultValue)));
    }

    private long longProperty(Properties config, String name, long defaultValue) {
        return Long.parseLong(config.getProperty(name, Long.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties config, String name, boolean defaultValue) {
        return Boolean.parseBoolean(config.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Position playerProperty(Properties config, String name, String defaultValue) {
        String string = config.getProperty(name, defaultValue);
        for (int i = 0; i < string.length();i++){
            if (string.charAt(i) == 'x'){
                return new Position(Integer.parseInt(string.substring(0, i)), Integer.parseInt(string.substring(i+1)));
            }
        }
        return new Position(0,0); // Default value is 0x0
    }

    private String worldProperty(Properties config, String name, String defaultValue) {
        return config.getProperty(name, defaultValue);
    }

    public GameLauncherView(Stage stage)  {
        MapRepoStringRLE mapRepoStringRLE = new MapRepoStringRLE();
        // Create menu
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem loadItem = new MenuItem("Load from file ...");
        MenuItem defaultItem = new MenuItem("Load default configuration");
        // To complete if we have time // Export as file MenuItem exportItem = new MenuItem("Export as file ...");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        menuFile.getItems().addAll(
                loadItem, defaultItem, /* To complete if we have time // Export as file exportItem,*/ new SeparatorMenuItem(),
                exitItem);

        menuBar.getMenus().addAll(menuFile);
        this.setTop(menuBar);

        Text text = new Text("UBomb 2022");
        text.getStyleClass().add("message");
        VBox scene = new VBox();
        scene.getChildren().add(text);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        scene.getStyleClass().add("message");
        this.setCenter(scene);

        // Load from file
        loadItem.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    Reader in = new FileReader(file);
                    Properties config = new Properties();
                    try {
                        config.load(in);
                        Position playerPosition = playerProperty(config, "player", "0x0");
                        boolean compression = booleanProperty(config, "compression", false);
                        int bombBagCapacity = integerProperty(config, "bombBagCapacity", 3);
                        int playerLives = integerProperty(config, "playerLives", 5);
                        long playerInvinsibilityTime = longProperty(config, "playerInvinsibilityTime", 4000);
                        int monsterVelocity = integerProperty(config, "monsterVelocity", 5);
                        long monsterInvincibilityTime = longProperty(config, "monsterInvincibilityTime", 1000);
                        configuration = new Configuration(playerPosition, bombBagCapacity, playerLives, playerInvinsibilityTime, monsterVelocity, monsterInvincibilityTime);
                        
                        int worldNumber = integerProperty(config, "levels", 1);
                        String[] worldString = new String[worldNumber];
                        for(int i = 0; i < worldNumber; i++){
                            worldString[i] = worldProperty(config, "level"+(i+1), "x");
                            this.mapLevel[i] = new MapLevel(0, 0);
                            this.mapLevel[i] = mapRepoStringRLE.load(worldString[i], compression);
                        }
                        
                        Game game = GameLauncher.load(configuration, this.mapLevel[0]);
                        game.setWorldString(worldString);
                        game.setMaxLevel(worldNumber);
                        game.setCompression(compression);
                        game.setMonstersTimers(monsterInvincibilityTime);
                        GameEngine engine = new GameEngine(game, stage);
                        engine.start();
                    } catch(IOException IOex){
                        return;
                    }

                } catch(FileNotFoundException FNFEex) {
                    return;
                }
            }
        });

        defaultItem.setOnAction(e -> {
            Game game = GameLauncher.loadDefault();
            GameEngine engine = new GameEngine(game, stage);
            engine.start();
        });

        // Exit
        exitItem.setOnAction(e -> System.exit(0));

    }


}
