package com.skeleton.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.skeleton.game.core.Entity;

public class MazeGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture skull;
    Texture mud, stone, potato;
    Vector2 playerPosition;
    private static final float PLAYER_SPEED = 5000.0f;
    BitmapFont font;

    float screenWidth;
    float screenHeight;

    float SCALE = 1f;

    private OrthographicCamera cam;
    final float VIRTUAL_HEIGHT = 256f;

    private static final int NUM_TILES_X = 16;
    private static final int NUM_TILES_Y = 21;
    private static final int TILE_SIZE = 16;

    private int[][] mapDataArray = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,1,1,0,0,0,1,1,0,0,0,0,0,1,1,0,0,0,1},
            {1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
            {1,1,1,0,1,0,1,0,0,0,0,0,0,2,0,1,0,1,1,0,1},
            {1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,1,0,1,1,1,1},
            {1,0,1,0,1,0,1,0,1,0,0,0,0,0,0,0,0,0,0,1,1},
            {1,0,0,0,1,0,1,0,1,1,0,0,2,0,0,1,1,1,0,1,1},
            {1,0,1,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1},
            {1,0,1,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,1},
            {1,0,1,0,1,1,1,1,0,1,2,0,0,0,0,0,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,1,0,1,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    List<Entity> mapData;
    List<Entity> entities;

    World world;
    Matrix4 debugMatrix;
    Box2DDebugRenderer debugRenderer;

    Entity player;

    private float debugCoolDown = 0;
    private boolean showDebug = false;

	@Override
	public void create () {

        world = new World(new Vector2(0, 0), true);
        world.setContinuousPhysics(true);


		batch = new SpriteBatch();
        skull = new Texture("wizard.png");
        mud = new Texture("mud-tile.png");
        stone = new Texture("stone-tile.png");
        potato = new Texture("potato-tile.png");
        playerPosition = new Vector2(0,0);

        FileHandle handle = Gdx.files.internal("MavenPro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        font = generator.generateFont(parameter);
        cam = new OrthographicCamera();
        debugMatrix = new Matrix4(cam.combined);

        debugRenderer = new Box2DDebugRenderer();

        entities = new ArrayList<Entity>();
        mapData = loadMap(mapDataArray);

        player = createPlayer();
        resetGame();
	}

    private List<Entity> loadMap(int[][] mapDataArray) {


        List<Entity> mapTiles = new ArrayList<Entity>();
        for (int i = 0; i < NUM_TILES_X; i++) {
            for (int j = 0; j < NUM_TILES_Y; j++) {

                if (mapDataArray[i][j] == 0) {
                    Sprite sprite = new Sprite(mud);
                    sprite.setPosition(j * TILE_SIZE, i * TILE_SIZE);
                    mapTiles.add(new Entity(sprite, null));
                    continue;
                }
                if (mapDataArray[i][j] == 1) {
                    Sprite sprite = new Sprite(stone);
                    sprite.setPosition(j * TILE_SIZE, i * TILE_SIZE);

                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.StaticBody;
                    bodyDef.position.set(sprite.getX(), sprite.getY());
                    Body body = world.createBody(bodyDef);
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(sprite.getWidth()/2, sprite.getHeight()/2);

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = shape;
                    fixtureDef.density = 1f;
                    fixtureDef.friction = 0f;

                    Fixture fixture = body.createFixture(fixtureDef);

                    shape.dispose();
                    mapTiles.add(new Entity(sprite, body));
                    continue;
                }
                if (mapDataArray[i][j] == 2) {
                    // mud tile
                    Sprite sprite = new Sprite(mud);
                    sprite.setPosition(j * TILE_SIZE, i * TILE_SIZE);
                    mapTiles.add(new Entity(sprite, null));

                    // potato
                    sprite = new Sprite(potato);
                    sprite.setPosition(j * TILE_SIZE, i * TILE_SIZE);

                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.DynamicBody;
                    bodyDef.position.set(sprite.getX(), sprite.getY());
                    Body body = world.createBody(bodyDef);
                    body.setLinearDamping(3.0f);
                    CircleShape shape = new CircleShape();
                    shape.setRadius(6);

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = shape;
                    fixtureDef.density = 0.1f;
                    fixtureDef.friction = 0f;


                    Fixture fixture = body.createFixture(fixtureDef);

                    shape.dispose();
                    entities.add(new Entity(sprite, body));
                }
            }
        }
        return mapTiles;
    }

    private Vector2 getRandomPosition() {
        float xpos = MathUtils.random(0, screenWidth);
        float ypos = MathUtils.random(0, screenHeight);
        return new Vector2(xpos, ypos);
    }


    public Entity createPlayer() {
        Sprite sprite = new Sprite(new Texture("wizard.png"));
        sprite.setPosition(16, 16);


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(sprite.getX(), sprite.getY());

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        body.setBullet(true);
        body.setLinearDamping(3.0f);

        CircleShape shape = new CircleShape();
        shape.setRadius(6);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.1f;

        Fixture fixture = body.createFixture(fixtureDef);


        shape.dispose();

        Entity player = new Entity(sprite, body);
        return player;
    }

    public float scale(float valueToBeScaled) {
        return valueToBeScaled/SCALE;
    }

    public void resize (int width, int height) {
        screenWidth = VIRTUAL_HEIGHT * width / (float)height;
        screenHeight = VIRTUAL_HEIGHT;
        cam.setToOrtho(false, scale(VIRTUAL_HEIGHT * width / (float)height), scale(VIRTUAL_HEIGHT));
        batch.setProjectionMatrix(cam.combined);
        debugMatrix = new Matrix4(cam.combined);
    }

	@Override
	public void render () {
        player.body.setAwake(true);
        handleInput();
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        player.body.setAwake(true);


		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

        for (Entity entity : mapData) {
            batch.draw(entity.sprite, entity.sprite.getX(), entity.sprite.getY());
        }
        for (Entity entity : entities) {
            entity.update();
            batch.draw(entity.sprite, entity.sprite.getX(), entity.sprite.getY());
        }

        player.update();
        batch.draw(player.sprite, player.sprite.getX(), player.sprite.getY());

        font.draw(batch, "!!! STEAL THE POTATOES !!!", screenWidth * 0.3f, screenHeight - 10);

        if (showDebug) {
            debugRenderer.render(world, debugMatrix);
        }
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        skull.dispose();
        font.dispose();
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

        Vector2 pos = player.body.getPosition();
//        player.body.setLinearVelocity(0, 0);
        if (isLeftPressed) {
            player.body.applyLinearImpulse(-actualSpeed, 0, pos.x, pos.y, true);
//            player.body.setLinearVelocity(-actualSpeed, 0);
        }
        if (isRightPressed) {
            player.body.applyLinearImpulse(actualSpeed, 0, pos.x, pos.y, true);
//            player.body.setLinearVelocity(actualSpeed, 0);
        }
        if (isUpPressed) {
            player.body.applyLinearImpulse(0, actualSpeed, pos.x, pos.y, true);
//            player.body.setLinearVelocity(0, actualSpeed);
        }
        if (isDownPressed) {
            player.body.applyLinearImpulse(0, -actualSpeed, pos.x, pos.y, true);
//            player.body.setLinearVelocity(0, -actualSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        debugCoolDown = debugCoolDown - Gdx.graphics.getDeltaTime();
        if (debugCoolDown < 0 && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            debugCoolDown = 1.0f;
            showDebug = !showDebug;
        }
	}
}
