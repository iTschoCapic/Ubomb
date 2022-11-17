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
    private MapLevel mapLevel = new MapLevel(0,0);
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

    public GameLauncherView(Stage stage)  {
        MapRepoStringRLE mapRepoStringRLE = new MapRepoStringRLE();
        // Create menu
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem loadItem = new MenuItem("Load from file ...");
        MenuItem defaultItem = new MenuItem("Load default configuration");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        menuFile.getItems().addAll(
                loadItem, defaultItem, new SeparatorMenuItem(),
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
                        int bombBagCapacity = integerProperty(config, "bombBagCapacity", 3);
                        int playerLives = integerProperty(config, "playerLives", 5);
                        long playerInvisibilityTime = longProperty(config, "playerInvisibilityTime", 4000);
                        int monsterVelocity = integerProperty(config, "monsterVelocity", 5);
                        long monsterInvisibilityTime = longProperty(config, "monsterInvisibilityTime", 1000);
                        Configuration configuration = new Configuration(new Position(0, 0), bombBagCapacity, playerLives, playerInvisibilityTime, monsterVelocity, monsterInvisibilityTime);
                        this.mapLevel = mapRepoStringRLE.load("_4B_9_x_T_9_4x____SBBS5_3x_T__SBBS_3S_3x_T__SH_S_3S___x_4S4_3S_3x_9__S_3x_5__9xMT_5T3_5x_TT_T__T__B_4x__T3BTTHB_S___x_5_HTTK_S_3x__B__BBS4T_3x_9_4T_x_9_5Tx");
                        Game game = GameLauncher.load(configuration, this.mapLevel);
                        GameEngine engine = new GameEngine(game, stage);
                        engine.start();
                        //updateGrid(mapLevel);// Chargement depuis un fichier (avec compression)
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
