package com.skeleton.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CromwellGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Vector2 playerPosition;
    private static final float PLAYER_SPEED = 22.0f;
    private BitmapFont font;

    private float screenWidth;
    private float screenHeight;
    private float time;

    private Animation walkAnimation;
    private TextureRegion[] walkFrames;
    private Texture walkSheet;
    private TextureRegion currentFrame;

    private TextureRegion standing;

    private float stateTime;
    private OrthographicCamera cam;

    final float VIRTUAL_HEIGHT = 128f;
    private boolean isMoving = false;
    private boolean isRight = true;

	@Override
	public void create () {
        standing = new TextureRegion(new Texture(Gdx.files.internal("standing.png")));
        walkSheet = new Texture(Gdx.files.internal("walk-test-gg2.png"));
        int numberOfFrames = 8;
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/numberOfFrames, walkSheet.getHeight());
        walkFrames = new TextureRegion[numberOfFrames];
        int index = 0;
        for (int j = 0; j < numberOfFrames; j++) {
            walkFrames[index++] = tmp[0][j];
        }
        walkAnimation = new Animation(0.1f, walkFrames);
        stateTime = 0f;

		batch = new SpriteBatch();
        playerPosition = getRandomPosition();

        FileHandle handle = Gdx.files.internal("MavenPro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        font = generator.generateFont(parameter);

        screenWidth = 128f;
        screenHeight = 128f;

        cam = new OrthographicCamera();
	}

    private Vector2 getRandomPosition() {
        float xpos = MathUtils.random(0, screenWidth);
        float ypos = MathUtils.random(0, screenHeight);
        return new Vector2(xpos, ypos);
    }

    public void resize (int width, int height) {
        cam.setToOrtho(false, VIRTUAL_HEIGHT * width / (float)height, VIRTUAL_HEIGHT);
        batch.setProjectionMatrix(cam.combined);
    }

	@Override
	public void render () {
        stateTime += Gdx.graphics.getDeltaTime();
        handleInput();
        update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
        if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, playerPosition.x, playerPosition.y);
        } else {
            batch.draw(standing, playerPosition.x, playerPosition.y);
        }
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

        isMoving = false;
        if (isLeftPressed) {
            playerPosition.add(-actualSpeed, 0);
            isMoving = true;
            if (isRight) {
                flipAnimation();
            }
            isRight = false;
        }
        if (isRightPressed) {
            playerPosition.add(actualSpeed, 0);
            isMoving = true;
            if (!isRight) {
                flipAnimation();
            }
            isRight = true;
        }
        if (isUpPressed) {
            playerPosition.add(0, actualSpeed);
            isMoving = true;
        }
        if (isDownPressed) {
            playerPosition.add(0, -actualSpeed);
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (time < 0 && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            resetGame();
        }
	}

	private void flipAnimation() {
        for (TextureRegion frames : walkAnimation.getKeyFrames()) {
            frames.flip(true, false);
        }
        standing.flip(true, false);
    }
}
