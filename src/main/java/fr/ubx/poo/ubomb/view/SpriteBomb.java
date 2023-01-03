package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.go.decor.bonus.Bomb;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteBomb extends Sprite {

    public SpriteBomb(Pane layer, Bomb bomb) {
        super(layer, null, bomb);
        updateImage();
    }

    @Override
    public void updateImage() {
        Bomb bomb = (Bomb) getGameObject();
        if (bomb.getDigit() == -1){
            return;
        }
        Image image = ImageResourceFactory.getBomb(bomb.getDigit()).getImage();
        setImage(image);
    }
}

