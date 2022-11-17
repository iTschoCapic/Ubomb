/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.go.decor.*;
import javafx.scene.layout.Pane;

import static fr.ubx.poo.ubomb.view.ImageResource.*;


public final class SpriteFactory {

    public static Sprite create(Pane layer, GameObject gameObject) {
        if (gameObject instanceof Stone)
            return new Sprite(layer, STONE.getImage(), gameObject);
        if (gameObject instanceof Tree)
            return new Sprite(layer, TREE.getImage(), gameObject);
        if (gameObject instanceof Key)
            return new Sprite(layer, KEY.getImage(), gameObject);
        if (gameObject instanceof Box)
            return new Sprite(layer, BOX.getImage(), gameObject);
        if (gameObject instanceof Heart)
            return new Sprite(layer, HEART.getImage(), gameObject);
        if (gameObject instanceof Princess)
            return new Sprite(layer, PRINCESS.getImage(), gameObject);
        if (gameObject instanceof Door) {
            if (((Door)gameObject).isOpen()) {
                if (((Door)gameObject).isSuperior())
                    return new Sprite(layer, DOOR_OPENED_PLUS.getImage(), gameObject);
                else
                    return new Sprite(layer, DOOR_OPENED_MINUS.getImage(), gameObject);
            }
            else {
                if (((Door)gameObject).isSuperior())
                    return new Sprite(layer, DOOR_CLOSED_PLUS.getImage(), gameObject);
                else
                    return new Sprite(layer, DOOR_CLOSED_MINUS.getImage(), gameObject);
            }
        }
        throw new RuntimeException("Unsupported sprite for decor " + gameObject);
    }
}
