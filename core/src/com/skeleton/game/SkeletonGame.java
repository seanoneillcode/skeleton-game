package com.skeleton.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class SkeletonGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture skull;
    Texture bone;
    Vector2 playerPosition;
    private static final float PLAYER_SPEED = 200.0f;
    BitmapFont font;
    int pickedBones;

    Vector2 bonePos;
    float screenWidth;
    float screenHeight;
    float time;

	@Override
	public void create () {
		batch = new SpriteBatch();
        skull = new Texture("skull.png");
        bone = new Texture("bone.png");
        playerPosition = getRandomPosition();

        FileHandle handle = Gdx.files.internal("MavenPro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        font = generator.generateFont(parameter);
        pickedBones = 0;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        bonePos = getRandomPosition();
        time = 10;
	}

    private Vector2 getRandomPosition() {
        float xpos = MathUtils.random(30, screenWidth - 30);
        float ypos = MathUtils.random(30, screenHeight - 30);
        return new Vector2(xpos, ypos);
    }


	@Override
	public void render () {

        handleInput();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

        if (time > 0) {
            update();
            batch.draw(skull, playerPosition.x, playerPosition.y);
            batch.draw(bone, bonePos.x, bonePos.y);
            font.draw(batch, "You've picked up : " + pickedBones, 10, 20);
            font.draw(batch, "time left : " + (int)(time), screenWidth - 100, 20);
            font.draw(batch, "!!! PICK UP ALL YOUR BONES !!!", screenWidth * 0.3f, screenHeight - 10);
        } else {
            float xmid = (screenWidth * 0.5f) - 80;
            float ymid = screenHeight * 0.5f;
            font.draw(batch, "Time is up!", xmid, ymid + 130);
            batch.draw(skull, xmid, ymid);
            font.draw(batch, "your score is : " + pickedBones, xmid, ymid - 40);
            font.draw(batch, "PRESS SPACE TO PLAY AGAIN", xmid - 40, ymid - 120);
        }

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        skull.dispose();
        font.dispose();
        bone.dispose();
	}

    private void update() {
        time = time - Gdx.graphics.getDeltaTime();
        if (playerPosition.x < 0) {
            playerPosition.x = 0;
        }
        if (playerPosition.x > screenWidth - 100) {
            playerPosition.x = screenWidth - 100;
        }
        if (playerPosition.y < 0) {
            playerPosition.y = 0;
        }
        if (playerPosition.y > screenHeight - 100) {
            playerPosition.y = screenHeight - 100;
        }

        Vector2 pmid = playerPosition.cpy().add(50, 50);
        Vector2 bmid = bonePos.cpy().add(10, 10);
        if (pmid.dst2(bmid) < 2000) {
            this.pickedBones++;
            bonePos = getRandomPosition();
        }
    }

    private void resetGame() {
        pickedBones = 0;
        time = 10;
        bonePos = getRandomPosition();
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
        }
        if (isRightPressed) {
            playerPosition.add(actualSpeed, 0);
        }
        if (isUpPressed) {
            playerPosition.add(0, actualSpeed);
        }
        if (isDownPressed) {
            playerPosition.add(0, -actualSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (time < 0 && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            resetGame();
        }
	}
}
