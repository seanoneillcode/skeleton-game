package com.skeleton.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    Sprite sprite;
    int health;
    float speed;

    public Enemy(Texture texture, Vector2 pos, int health, float speed) {
        this.sprite = new Sprite(texture);
        sprite.setPosition(pos.x, pos.y);
        this.health = health;
        this.speed = speed;
    }

    public boolean shouldRemove() {
        return health < 1;
    }

    public void update(Vector2 player) {
        Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
        float delta = Gdx.graphics.getDeltaTime();
        float newX = sprite.getX() + (delta * dir.x * speed);
        float newY = sprite.getY() + (delta * dir.y * speed);
        sprite.setPosition(newX, newY);
    }
}
