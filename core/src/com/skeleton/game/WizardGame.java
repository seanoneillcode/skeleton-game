package com.skeleton.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class WizardGame extends ApplicationAdapter {

    static final int WORLD_WIDTH = 256;
    static final int WORLD_HEIGHT = 256;

	SpriteBatch batch;
    TextureRegion wizard;
    Texture bolt;
    Vector2 playerPosition;
    private static final float PLAYER_SPEED = 32.0f;
    private static final float BULLET_SPEED = 80f;
    BitmapFont font;
    int pickedBones;

    float screenWidth;
    float screenHeight;
    float time;
    OrthographicCamera camera;

    boolean isRight = true;

    private List<Bullet> bullets;
    float shootCooldown;
    private static final float MAX_COOLDOWN = 0.2f;

	@Override
	public void create () {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

		batch = new SpriteBatch();
        wizard = new TextureRegion(new Texture("wizard.png"));
        bolt = new Texture("bolt.png");
        playerPosition = getRandomPosition();

        FileHandle handle = Gdx.files.internal("MavenPro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 10;
        font = generator.generateFont(parameter);
        pickedBones = 0;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        bullets = new ArrayList<Bullet>();
	}

    private Vector2 getRandomPosition() {
        float xpos = MathUtils.random(0, screenWidth);
        float ypos = MathUtils.random(0, screenHeight);
        return new Vector2(xpos, ypos);
    }


    private void addBullet(Vector2 dir, Vector2 pos) {
        Bullet b = new Bullet(bolt, dir, pos);
        bullets.add(b);
    }

	@Override
	public void render () {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        handleInput();
        update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        batch.draw(wizard, playerPosition.x, playerPosition.y);
        for (Bullet b : bullets) {
            b.sprite.draw(batch);
        }
        font.draw(batch, "WIZARD GAME", 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        font.dispose();
	}

    private void update() {
        time = time - Gdx.graphics.getDeltaTime();
        if (playerPosition.x < 0) {
            playerPosition.x = 0;
        }
        if (playerPosition.x > screenWidth) {
            playerPosition.x = screenWidth;
        }
        if (playerPosition.y < 0) {
            playerPosition.y = 0;
        }
        if (playerPosition.y > screenHeight) {
            playerPosition.y = screenHeight;
        }
        Iterator<Bullet> iter = bullets.listIterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.update();
            if (bullet.ttl < 0) {
                iter.remove();
            }
        }

//        Vector2 pmid = playerPosition.cpy().add(50, 50);
//        Vector2 bmid = bonePos.cpy().add(10, 10);
//        if (pmid.dst2(bmid) < 2000) {
//            this.pickedBones++;
//            bonePos = getRandomPosition();
//        }
    }

    private void resetGame() {
        pickedBones = 0;
        time = 10;
        playerPosition = getRandomPosition();
    }

	private void handleInput() {
        float actualSpeed = PLAYER_SPEED * Gdx.graphics.getDeltaTime();

		boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean isUpPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean isDownPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (isLeftPressed) {
            playerPosition.add(-actualSpeed, 0);
            if (isRight) {
                wizard.flip(true, false);
            }
            isRight = false;
        }
        if (isRightPressed) {
            playerPosition.add(actualSpeed, 0);
            if (!isRight) {
                wizard.flip(true, false);
            }
            isRight = true;
        }
        if (isUpPressed) {
            playerPosition.add(0, actualSpeed);
        }
        if (isDownPressed) {
            playerPosition.add(0, -actualSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (shootCooldown < 0) {
                shootCooldown = MAX_COOLDOWN;
                if (isRight) {
                    Vector2 offset = playerPosition.cpy().add(15, 2);
                    addBullet(new Vector2(BULLET_SPEED, 0), offset);
                } else {
                    Vector2 offset = playerPosition.cpy().add(-5, 2);
                    addBullet(new Vector2(-BULLET_SPEED, 0), offset);
                }

            } else {
                shootCooldown = shootCooldown - Gdx.graphics.getDeltaTime();
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
	}
}
