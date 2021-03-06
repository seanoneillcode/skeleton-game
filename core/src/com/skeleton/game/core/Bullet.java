package com.skeleton.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Bullet {

    public float ttl;
    public Sprite sprite;
    public Vector2 dir;

    public Bullet(Texture texture, Vector2 dir, Vector2 pos) {
        sprite = new Sprite(texture);
        sprite.setPosition(pos.x, pos.y);
        ttl = 2f;
        this.dir = dir;
    }

    public boolean shouldRemove() {
        return ttl < 0;
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        ttl = ttl - delta;
        float newX = sprite.getX() + (delta * dir.x);
        float newY = sprite.getY() + (delta * dir.y);
        sprite.setPosition(newX, newY);
    }
}
