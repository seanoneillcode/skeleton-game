package com.skeleton.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class WizardGame extends ApplicationAdapter {

    private static final int WORLD_WIDTH = 256;
    private static final int WORLD_HEIGHT = 256;

    private SpriteBatch batch;
    private TextureRegion wizard;
    private TextureRegion downWizard;
    private TextureRegion upWizard;

    private Texture skeleton;
    private Texture bolt;
    private Vector2 playerPosition;
    private static final float PLAYER_SPEED = 32.0f;
    private static final float BULLET_SPEED = 80f;
    private static final float SKELETON_SPEED = 32f;
    private BitmapFont font;

    private float screenWidth;
    private float screenHeight;
    private float time;
    private OrthographicCamera camera;

    private boolean isRight = true;

    private List<Bullet> bullets;
    private List<Enemy> enemies;
    private float shootCooldown;
    private static final float MAX_COOLDOWN = 0.2f;

    private DIR playerDir = DIR.RIGHT;

    private int numberOfSkeletons = 4;
    private int wizardLife = 3;
    private int enemiesKilled = 0;

    Sound wizardDeathSound;
    Sound wizardShootSound;
    Sound deathScreamSound;
    Sound enemyDeathSound;
    Sound loopSound;
    Sound loopSound2;

    Preferences prefs;
    int previousHighScore;

    float hurtCooldown = 0;
    private static final float HURT_COOL_DOWN = 1f;
    float waitStart = 0;
    private static final float WAIT_START_COOLDOWN = 2.0f;
    private boolean started;

    @Override
	public void create () {
        prefs = Gdx.app.getPreferences("WizardGamePreferences");
        previousHighScore = prefs.getInteger("highscore");

        enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("death-scream.wav"));
        wizardShootSound = Gdx.audio.newSound(Gdx.files.internal("wizard-shoot.wav"));
        wizardDeathSound = Gdx.audio.newSound(Gdx.files.internal("wizard-hurt.wav"));
        loopSound = Gdx.audio.newSound(Gdx.files.internal("bad-loop.wav"));
        loopSound.loop(1.0f);
        loopSound2 = Gdx.audio.newSound(Gdx.files.internal("bad-loop2.wav"));
        loopSound2.loop(1.0f);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

		batch = new SpriteBatch();
        wizard = new TextureRegion(new Texture("wizard.png"));
        upWizard = new TextureRegion(new Texture("wizard-up.png"));
        downWizard = new TextureRegion(new Texture("wizard-down.png"));
        bolt = new Texture("bolt.png");
        skeleton = new Texture("skeleton.png");
        playerPosition = getRandomPosition();

        FileHandle handle = Gdx.files.internal("MavenPro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 10;
        font = generator.generateFont(parameter);
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        resetGame();
	}

    private Vector2 getRandomPosition() {
        float xpos = MathUtils.random(0, screenWidth);
        float ypos = MathUtils.random(0, screenHeight);
        return new Vector2(xpos, ypos);
    }


    private void addBullet(Vector2 dir, Vector2 pos) {
        Bullet b = new Bullet(bolt, dir, pos);
        bullets.add(b);
        wizardShootSound.play(0.6f, MathUtils.random(0.8f,1.2f), 0.5f);
    }

    private void addSkeleton() {
        Vector2 pos = null;
        switch (MathUtils.random(3)) {
            case 0:
                pos = new Vector2(MathUtils.random(0, 256), 0);
                break;
            case 1:
                pos = new Vector2(MathUtils.random(0, 256), 256);
                break;
            case 2:
                pos = new Vector2(0, MathUtils.random(0, 256));
                break;
            case 3:
                pos = new Vector2(256, MathUtils.random(0, 256));
                break;
        }
        Enemy e = new Enemy(skeleton, pos, 1, SKELETON_SPEED);
        enemies.add(e);
    }

	@Override
	public void render () {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        handleInput();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        if (wizardLife > 0) {
            update();
            switch (playerDir) {
                case RIGHT:
                case LEFT:
                    batch.draw(wizard, playerPosition.x, playerPosition.y);
                    break;
                case UP:
                    batch.draw(upWizard, playerPosition.x, playerPosition.y);
                    break;
                case DOWN:
                    batch.draw(downWizard, playerPosition.x, playerPosition.y);
                    break;
            }
            for (Bullet b : bullets) {
                b.sprite.draw(batch);
            }
            for (Enemy e : enemies) {
                e.sprite.draw(batch);
            }
            font.draw(batch, "SOULS : " + wizardLife, 200, 180);
            font.draw(batch, "DESTROYED : " + enemiesKilled, 4, 180);
        } else {
            font.draw(batch, "YOU RAN OUT OF SOULS", 80, 158);
            font.draw(batch, "YOU DESTROYED  " + enemiesKilled + "  ENEMIES", 70, 128);
            font.draw(batch, "PRESS SPACE TO PLAY AGAIN", 70, 68);
            if (enemiesKilled == previousHighScore) {
                font.draw(batch, "NEW HIGH SCORE!", 70, 98);
            } else {
                font.draw(batch, "CURRENT HIGH SCORE is " + this.previousHighScore, 70, 98);
            }
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
        Iterator<Bullet> iter = bullets.listIterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.update();
            if (bullet.shouldRemove()) {
                iter.remove();
            }
            if (enemyCollision(bullet.sprite.getBoundingRectangle())) {
                bullet.ttl = -1;
            }
        }

        Rectangle rect = new Rectangle(playerPosition.x, playerPosition.y, 16, 16);

        if (enemyCollision(rect)) {
            wizardLife = wizardLife - 1;
            wizardDeathSound.play(1.0f);
            hurtCooldown = HURT_COOL_DOWN;
            if (wizardLife < 1) {
                if (enemiesKilled > previousHighScore) {
                    prefs.putInteger("highscore", enemiesKilled);
                    previousHighScore = enemiesKilled;
                }
            }
        }
        hurtCooldown = hurtCooldown - Gdx.graphics.getDeltaTime();

        Iterator<Enemy> iter2 = enemies.listIterator();
        while (iter2.hasNext()) {
            Enemy enemy = iter2.next();
            enemy.update(playerPosition);
            if (enemy.shouldRemove()) {
                iter2.remove();
                enemiesKilled = enemiesKilled + 1;
                if (hurtCooldown < 0) {
                    enemyDeathSound.play(0.3f, MathUtils.random(0.8f,1.2f), 0.5f);
                }
            }
        }
        if (enemies.size() < 1 && started) {
            numberOfSkeletons++;
            addWaveOfSkeletons();
        }
        waitStart = waitStart - Gdx.graphics.getDeltaTime();
        if (waitStart < 0 && !started) {
            started = true;
            addWaveOfSkeletons();
        }
    }

    private boolean enemyCollision(Rectangle rect) {
        boolean isColliding = false;
        for (Enemy e : enemies) {
            if(rect.overlaps(e.sprite.getBoundingRectangle())) {
                e.health = e.health - 1;
                isColliding = true;
            }
        }
        return isColliding;
    }

    private void resetGame() {
        waitStart = WAIT_START_COOLDOWN;
        wizardLife = 3;
        playerPosition = new Vector2(128, 128);
        enemies.clear();
        bullets.clear();
        enemiesKilled = 0;
        numberOfSkeletons = 4;
        started = false;
    }

    private void addWaveOfSkeletons() {
        for (int i = 0; i < numberOfSkeletons; i++) {
            addSkeleton();
        }
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
        Vector2 offset = null;
        Vector2 dir = null;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            offset = playerPosition.cpy().add(3, 15);
            dir = new Vector2(0, BULLET_SPEED);
            playerDir = DIR.UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            offset = playerPosition.cpy().add(3, -8);
            dir = new Vector2(0, -BULLET_SPEED);
            playerDir = DIR.DOWN;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            offset = playerPosition.cpy().add(-5, 2);
            dir = new Vector2(-BULLET_SPEED, 0);
            if (isRight) {
                wizard.flip(true, false);
            }
            isRight = false;
            playerDir = DIR.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            offset = playerPosition.cpy().add(15, 2);
            dir = new Vector2(BULLET_SPEED, 0);
            if (!isRight) {
                wizard.flip(true, false);
            }
            isRight = true;
            playerDir = DIR.RIGHT;
        }
        if (offset != null && dir != null) {
            if (shootCooldown < 0) {
                shootCooldown = MAX_COOLDOWN;
                addBullet(dir, offset);
            }
        }
        shootCooldown = shootCooldown - Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (wizardLife < 1 && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            resetGame();
        }
	}

	private enum DIR {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
