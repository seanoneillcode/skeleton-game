package com.skeleton.game.core;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class Entity {
    public Sprite sprite;
    public Body body;

    public Entity(Sprite sprite, Body body) {
        this.sprite = sprite;
        this.body = body;
    }

    public void update() {
        if (body != null) {
            sprite.setPosition(body.getPosition().x, body.getPosition().y);
        }
    }
}
