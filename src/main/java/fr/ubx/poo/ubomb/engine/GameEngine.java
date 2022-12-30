/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.engine;

import fr.ubx.poo.ubomb.launcher.*;
import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.*;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.view.ImageResource;
import fr.ubx.poo.ubomb.view.Sprite;
import fr.ubx.poo.ubomb.view.SpriteFactory;
import fr.ubx.poo.ubomb.view.SpritePlayer;
import fr.ubx.poo.ubomb.view.SpriteMonster;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.launcher.GameLauncher;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public final class GameEngine {

    private static AnimationTimer gameLoop;
    private Game game;
    private final Player player;
    private ArrayList<Monster> monsters;
    private boolean isOnMonster;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private int currentLevel = 0;
    private Timer playerTimer = new Timer(4000);
    private int princessLevel;

    public GameEngine(Game game, final Stage stage) {
        this.stage = stage;
        this.game = game;
        this.player = game.player();
        this.monsters = game.getMonsters();
        this.isOnMonster = false;
        this.playerTimer = new Timer(game.configuration().playerInvincibilityTime());
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {

        Group root = new Group();
        layer = new Pane();

        int height = game.grid().height();
        int width = game.grid().width();
        int sceneWidth = width * ImageResource.size;
        int sceneHeight = height * ImageResource.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setScene(scene);
        stage.setResizable(true); // At first was false
        stage.sizeToScene();
        stage.hide();
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);

        this.monsters = game.getMonsters();

        sprites.clear();
        // Create sprites
        for (var decor : game.grid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
        }

        sprites.add(new SpritePlayer(layer, player));
        for (Monster monster : monsters){
            monster.setGame(this.game);
            sprites.add(new SpriteMonster(layer, monster));
            if (monster.game.getCurrentLevel() == currentLevel){
                monster.setRequestMove(true);
            }
        }

        if (game.princessInLevel()){
            game.setPrincessLevel(game.getCurrentLevel());
            for (Monster monster : monsters)
                monster.setRandom(false);
        } else {
            for (Monster monster : monsters)
                monster.setRandom(true);
        }
        
    }

    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);
                createNewBombs(now);
                checkCollision(now);
                checkExplosions();

                // Graphic update
                cleanupSprites();
                render();
                statusBar.update(game);

            }
        };
    }

    private void checkExplosions() {
        // Check explosions of bombs
    }

    private void animateExplosion(Position src, Position dst) {
        ImageView explosion = new ImageView(ImageResource.EXPLOSION.getImage());
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), explosion);
        tt.setFromX(src.x() * Sprite.size);
        tt.setFromY(src.y() * Sprite.size);
        tt.setToX(dst.x() * Sprite.size);
        tt.setToY(dst.y() * Sprite.size);
        tt.setOnFinished(e -> {
            layer.getChildren().remove(explosion);
        });
        layer.getChildren().add(explosion);
        tt.play();
    }

    private void createNewBombs(long now) {
        // Create a new Bomb is needed
    }

    private void checkCollision(long now) {
        List<GameObject> gameObjects = game.getGameObjects(player.getPosition());
        for (Monster monster : monsters) {
            if (monster.game.getCurrentLevel() == currentLevel){
                if (monster.getPosition().equals(player.getPosition())){
                    if (!isOnMonster && !playerTimer.isRunning()){
                        isOnMonster = true;
                        player.updateLives(-1);
                        playerTimer.start();
                    }
                } else {
                    isOnMonster = false;
                    if (game.grid().get(player.getPosition()) instanceof Princess) {
                        gameLoop.stop();
                        showMessage("Gagné!", Color.RED);
                    }
                }
            }
        }
        Decor gameDecor = game.grid().get(player.getPosition());
        Position nextPos = player.getDirection().nextPosition(player.getPosition());
        if (gameDecor instanceof Box) {
            ((Box)gameDecor).doMove(player.getDirection());
            game.grid().remove(player.getPosition());
            game.grid().set(nextPos, gameDecor);
        }
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
        } else if (input.isKey()) {
            player.useKey();
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }

    private void update(long now) {
        playerTimer.update(now);
        for (Monster monster : monsters){
            if (monster.getRequestMove() == true){
                monster.update(now);
            }
        }
        player.update(now);
        if (player.getLives() == 0) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (currentLevel != game.getCurrentLevel()) {
            if (game.getPrincessLevel() > game.getCurrentLevel() || game.getPrincessLevel() == -1){
                if (currentLevel - game.getCurrentLevel() > 0){
                    game.setMonsterVelocity(game.getMonsterVelocity(), -1, currentLevel);
                } else {
                    game.setMonsterVelocity(game.getMonsterVelocity(), 1, currentLevel);
                }
            } else {
                if (currentLevel - game.getCurrentLevel() > 0){
                    game.setMonsterVelocity(game.getMonsterVelocity(), 1, currentLevel);
                } else {
                    game.setMonsterVelocity(game.getMonsterVelocity(), -1, currentLevel);
                }
            }
            
            currentLevel = game.getCurrentLevel();
            for (Monster monster : monsters){
                if (currentLevel % 2 == 0){
                    if (monster.game.getCurrentLevel() == currentLevel){
                        monster.setLives((currentLevel/2)+1);
                    }
                } else {
                    monster.setLives(((currentLevel-1)/2)+1);
                }
            }
            initialize();
        }
    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                game.grid().remove(sprite.getPosition());
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    public void updateSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isModified()) {
                sprite.updateImage();
            }
        });
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }
}