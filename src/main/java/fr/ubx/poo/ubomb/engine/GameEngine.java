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
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.view.ImageResource;
import fr.ubx.poo.ubomb.view.Sprite;
import fr.ubx.poo.ubomb.view.SpriteFactory;
import fr.ubx.poo.ubomb.view.SpritePlayer;
import fr.ubx.poo.ubomb.view.SpriteMonster;
import fr.ubx.poo.ubomb.view.SpriteBomb;
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
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private int currentLevel = 0;
    private Timer playerTimer = new Timer(4000);
    private int princessLevel;
    private List<Bomb> bombs = new ArrayList<>();
    private boolean animationDone;
    private List<Bomb> removeBombs = new ArrayList<>();
    private List<Monster> monstersDeleted = new ArrayList<>();

    public GameEngine(Game game, final Stage stage) {
        this.stage = stage;
        this.game = game;
        this.player = game.player();
        this.monsters = game.getMonsters();
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
            if (!(decor instanceof Bomb)){
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
            }
        }

        bombs = game.getBombs();

        for (Bomb bomb : bombs){
            sprites.add(new SpriteBomb(layer, bomb));
        }
        
        for (Monster monster : monsters){
            monster.setGame(this.game);
            sprites.add(new SpriteMonster(layer, monster));
            if (monster.game.getCurrentLevel() == currentLevel){
                monster.setMoveRequested(true);
                monster.setTimer(game.configuration().monsterInvincibilityTime());
            }
        }

        sprites.add(new SpritePlayer(layer, player));

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
                checkCollision(now);
                checkExplosions(now);

                // Graphic update
                cleanupSprites();
                render();
                statusBar.update(game);

            }
        };
    }

    private void checkExplosions(long now) {
        if (!bombs.isEmpty()){
            for (Bomb bomb : bombs){
                bomb.getTimer().update(now);
                if (game.grid().get(bomb.getPosition()) instanceof Bomb && bomb.getTimer().remaining() <= 0){
                    if (bomb.getIsInWorld()){
                        animateExplosion(bomb.getPosition(), new Position(bomb.getPosition().getX(), bomb.getPosition().getY()));
                        animateExplosion(bomb.getPosition(), new Position(bomb.getPosition().getX()+1, bomb.getPosition().getY()));
                        animateExplosion(bomb.getPosition(), new Position(bomb.getPosition().getX(), bomb.getPosition().getY()+1));
                        animateExplosion(bomb.getPosition(), new Position(bomb.getPosition().getX()-1, bomb.getPosition().getY()));
                        animateExplosion(bomb.getPosition(), new Position(bomb.getPosition().getX(), bomb.getPosition().getY()-1));
                    } else {
                        bomb.isInWorld(true);
                        
                    }
                    checkRangeCollisions(game.player().getBombRange(), bomb, now);
                    game.grid().remove(bomb.getPosition());
                    removeBombs.add(bomb);
                    bomb.remove();
                    game.player().addAvailableBombs();
                    
                }
                bomb.setModified(true);
            }
            if (!removeBombs.isEmpty()){
                bombs.removeAll(removeBombs);
                sprites.remove(removeBombs);
                removeBombs.clear();
            }
        }
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
        if (game.grid().get(player.getPosition()) == null){
            Bomb bomb = new Bomb(player.getPosition());
            game.grid().set(player.getPosition(), bomb);
            sprites.add(new SpriteBomb(layer, bomb));
            bomb.getTimer().start();
            bombs.add(bomb);
        }
    }

    private void checkRangeCollisions(int range, Bomb bomb, long now){

        boolean notDestroyed1 = true;
        boolean notDestroyed2 = true;
        boolean notDestroyed3 = true;
        boolean notDestroyed4 = true;
        
        for (int i = 0; i < range;i++){
            Position pos1 = new Position(bomb.getPosition().getX()+range, bomb.getPosition().getY());
            Position pos2 = new Position(bomb.getPosition().getX(), bomb.getPosition().getY()+range);
            Position pos3 = new Position(bomb.getPosition().getX()-range, bomb.getPosition().getY());
            Position pos4 = new Position(bomb.getPosition().getX(), bomb.getPosition().getY()-range);

            playerTimer.update(now);
            if (!playerTimer.isRunning() && (player.getPosition().equals(pos1) || player.getPosition().equals(pos2) || player.getPosition().equals(pos3) || player.getPosition().equals(pos4) || player.getPosition().equals(bomb.getPosition()))){
                player.updateLives(-1);
                playerTimer.start();
            }

            for (Monster monster : monsters){
                monster.getTimer().update(now);
                if (!monster.getTimer().isRunning() && (monster.getPosition().equals(pos1) || monster.getPosition().equals(pos2) || monster.getPosition().equals(pos3) || monster.getPosition().equals(pos4) || player.getPosition().equals(bomb.getPosition()))){
                    monster.updateLives(-1);
                    monster.getTimer().start();
                }
            }
            GameObject obj1 = game.grid().get(pos1);
            GameObject obj2 = game.grid().get(pos2);
            GameObject obj3 = game.grid().get(pos3);
            GameObject obj4 = game.grid().get(pos4);
            if ((obj1 instanceof Box || obj1 instanceof Bonus) && notDestroyed1){
                obj1.remove();
                notDestroyed1 = false;
            }
            if ((obj2 instanceof Box || obj2 instanceof Bonus) && notDestroyed2){
                obj2.remove();
                notDestroyed2 = false;
            }
            if ((obj3 instanceof Box || obj3 instanceof Bonus) && notDestroyed3){
                obj3.remove();
                notDestroyed3 = false;
            }
            if ((obj4 instanceof Box || obj4 instanceof Bonus) && notDestroyed4){
                obj4.remove();
                notDestroyed4 = false;
            }
        }
        
    }

    private void checkCollision(long now) {
        List<GameObject> gameObjects = game.getGameObjects(player.getPosition());
        for (Monster monster : monsters) {
            if (monster.game.getCurrentLevel() == currentLevel){
                if (monster.getPosition().equals(player.getPosition())){
                    if (!playerTimer.isRunning()){
                        player.updateLives(-1);
                        playerTimer.start();
                    }
                } else {
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
        } else if (input.isBomb()){
            player.useBomb();
            createNewBombs(now);
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
            if (monster.getMoveRequested() == true){
                monster.update(now);
            }
            if (monster.getLives() == 0){
                monster.remove();
                monstersDeleted.add(monster);
            }
        }
        monsters.removeAll(monstersDeleted);
        monstersDeleted.clear();
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

            for (Bomb bomb : bombs){
                bomb.isInWorld(false);
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