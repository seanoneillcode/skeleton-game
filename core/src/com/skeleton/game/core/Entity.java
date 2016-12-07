package com.skeleton.game.core;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Entity {
    public Sprite sprite;
    public Body body;
    private static final float MAX_SPEED = 50.0f;

    public Entity(Sprite sprite, Body body) {
        this.sprite = sprite;
        this.body = body;
    }

    public void update() {
        if (body != null) {
            sprite.setPosition(body.getPosition().x, body.getPosition().y);
            Vector2 limitVel = body.getLinearVelocity();
            float speed = limitVel.len();
            if (speed > MAX_SPEED) {
                body.setLinearVelocity(limitVel.nor().scl(MAX_SPEED));
            }
        }
    }
}
