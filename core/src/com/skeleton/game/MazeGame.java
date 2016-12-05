package com.skeleton.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MazeGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture skull;
    Texture mud, stone;
    Vector2 playerPosition;
    private static final float PLAYER_SPEED = 80.0f;
    BitmapFont font;

    float screenWidth;
    float screenHeight;

    private OrthographicCamera cam;
    final float VIRTUAL_HEIGHT = 256f;

    private static final int NUM_TILES_X = 16;
    private static final int NUM_TILES_Y = 16;
    private static final int TILE_SIZE = 16;

    private int[][] mapData = {
            {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,0,1,0,1,1,1,1,0,1,0,0},
            {0,0,0,0,1,0,1,0,1,0,0,0,0,1,0,0},
            {1,1,1,0,1,0,1,0,0,0,1,0,1,1,0,0},
            {0,0,1,0,0,0,1,0,1,0,1,0,1,1,1,0},
            {0,0,1,0,1,0,1,0,1,0,0,0,0,0,1,0},
            {0,0,0,0,1,0,1,0,1,1,1,1,1,0,1,0},
            {0,0,1,0,1,0,0,0,0,1,0,0,0,0,0,0},
            {0,0,1,0,1,0,0,0,0,1,0,1,1,1,0,1},
            {0,0,1,0,1,0,0,0,0,1,0,0,0,1,0,1},
            {0,0,1,0,1,1,1,1,1,1,0,1,0,1,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
            {0,0,1,1,1,0,1,0,1,1,0,1,1,0,1,1},
            {0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0},
            {0,0,1,1,1,0,0,0,1,0,0,1,1,1,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };

	@Override
	public void create () {

		batch = new SpriteBatch();
        skull = new Texture("wizard.png");
        mud = new Texture("mud-tile.png");
        stone = new Texture("stone-tile.png");
        playerPosition = getRandomPosition();

        FileHandle handle = Gdx.files.internal("MavenPro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        font = generator.generateFont(parameter);
        cam = new OrthographicCamera();


        resetGame();
	}

    private Vector2 getRandomPosition() {
        float xpos = MathUtils.random(0, screenWidth);
        float ypos = MathUtils.random(0, screenHeight);
        return new Vector2(xpos, ypos);
    }


    public void resize (int width, int height) {
        screenWidth = VIRTUAL_HEIGHT * width / (float)height;
        screenHeight = VIRTUAL_HEIGHT;
        cam.setToOrtho(false, VIRTUAL_HEIGHT * width / (float)height, VIRTUAL_HEIGHT);
        batch.setProjectionMatrix(cam.combined);
    }

	@Override
	public void render () {

        handleInput();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

        update();

        for (int i = 0; i < NUM_TILES_X; i++) {
            for (int j = 0; j < NUM_TILES_Y; j++) {
                Texture t = mapData[i][j] == 0 ? mud : stone;
                batch.draw(t, i * TILE_SIZE, j * TILE_SIZE);
            }
        }

        batch.draw(skull, playerPosition.x, playerPosition.y);
        font.draw(batch, "!!! STEAL THE POTATOES !!!", screenWidth * 0.3f, screenHeight - 10);


		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        skull.dispose();
        font.dispose();
	}

    private void update() {
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
    }

    private void resetGame() {
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
	}
}
