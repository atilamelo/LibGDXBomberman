package com.mygdx.game.utils;

import java.util.List;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.box2d.BombUserData;
import com.mygdx.game.box2d.BombermanUserData;
import com.mygdx.game.box2d.BrickUserData;
import com.mygdx.game.box2d.ExplosionUserData;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.systems.BrickPlacement;

public class WorldUtils {
    public static World createWorld() {
        return new World(new Vector2(), true);
    }

    public static Body createBomberman() {
        // Body Def
        BodyDef bodyDef = new BodyDef();
        World world = GameManager.getInstance().getWorld();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(GameManager.BOMBERMAN_X, GameManager.BOMBERMAN_Y));
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0f;

        // Shape of Bomberman
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(GameManager.BOMBERMAN_B2D_WIDTH, GameManager.BOMBERMAN_B2D_HEIGHT);

        // Create body
        Body body = world.createBody(bodyDef);
        body.setSleepingAllowed(false);

        // Fixture Def
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 0.5f;
        fdef.friction = 0.0f;
        fdef.restitution = 0.0f;
        fdef.filter.categoryBits = GameManager.PLAYER_BIT;
        body.setActive(true);

        body.createFixture(fdef);
        body.resetMassData();
        BombermanUserData userData = new BombermanUserData(GameManager.BOMBERMAN_WIDTH, GameManager.BOMBERMAN_HEIGHT);
        body.setUserData(userData);
        shape.dispose();

        return body;
    }

    public static Body createBomb(float x, float y) {
        // Body Def
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));
        World world = GameManager.getInstance().getWorld();

        // Shape of Bomberman
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(GameManager.BOMB_B2D_WIDTH, GameManager.BOMB_B2D_HEIGHT);

        // Create body
        Body body = world.createBody(bodyDef);

        // Fixture Def
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.isSensor = true;
        fdef.filter.categoryBits = GameManager.BOMB_BIT;

        body.createFixture(fdef);
        body.resetMassData();
        body.setUserData(new BombUserData(GameManager.BOMB_WIDTH, GameManager.BOMB_HEIGHT));
        shape.dispose();

        return body;
    }

    /*
     * CRÉDITOS: Baseado na solução de Brent Aureli Code
     * https://www.youtube.com/watch?v=AmLDslUdepo&list=PLZm85UZQLd2SXQzsF-a0-
     * pPF6IWDDdrXt&index=7
     */
    public static void createMap(TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        World world = GameManager.getInstance().getWorld();
        Body body;

        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            rect.width = rect.width / GameManager.PPM;
            rect.height = rect.height / GameManager.PPM;
            rect.x = rect.x / GameManager.PPM;
            rect.y = rect.y / GameManager.PPM;

            bdef.type = BodyType.StaticBody;
            bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fdef.shape = shape;
            fdef.density = 1.0f;
            fdef.filter.categoryBits = GameManager.WALL_BIT;
            fdef.isSensor = false;
            body.createFixture(fdef);

        }

        shape.dispose();
    }

    public static Body createExplosion(float x, float y, float width, float height, boolean isCenter) {
        // Get world
        World world = GameManager.getInstance().getWorld();

        // Body Def
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));

        // Shape of Explosion
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(width, height);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(width + 0.1f);

        // Create body
        Body body = world.createBody(bodyDef);

        // Fixture Def
        FixtureDef fdef = new FixtureDef();
        if (isCenter) {
            fdef.shape = circleShape;
        } else {
            fdef.shape = boxShape;
        }
        fdef.filter.categoryBits = GameManager.EXPLOSION_BIT;
        fdef.isSensor = true;

        body.createFixture(fdef);
        body.resetMassData();
        body.setUserData(new ExplosionUserData(GameManager.EXPLOSION_WIDTH, GameManager.EXPLOSION_HEIGHT));
        boxShape.dispose();
        circleShape.dispose();

        return body;
    }

    public static void createBricks(GameStage stage) {
        List<BrickPlacement.Position> positions = BrickPlacement.generateBrickPositions();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        World world = GameManager.getInstance().getWorld();
        Body body;

        for (BrickPlacement.Position position : positions) {
            // Body Def
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(new Vector2(position.getX() + 0.5f, position.getY() + 0.5f));

            // Shape of Bomb
            shape = new PolygonShape();
            shape.setAsBox(GameManager.BRICK_B2D_WIDTH, GameManager.BRICK_B2D_HEIGHT);

            // Create body
            body = world.createBody(bdef);

            // Fixture Def
            fdef = new FixtureDef();
            fdef.shape = shape;
            fdef.filter.categoryBits = GameManager.BRICK_BIT;
            // fdef.isSensor = true;

            body.createFixture(fdef);
            body.resetMassData();
            body.setUserData(new BrickUserData(GameManager.BRICK_WIDTH, GameManager.BRICK_HEIGHT));

            new Brick(body, stage);
        }

        shape.dispose();
    }

    public static boolean hasObjectAtPosition(Vector2 position, short categoryBits) {
        World world = GameManager.getInstance().getWorld();
        final boolean[] hasBody = { false };
        final short categoryBitsFinal = categoryBits;

        // Define the AABB (Axis-Aligned Bounding Box) centered at the position
        float aabbHalfWidth = 0.05f;
        Vector2 lowerBound = new Vector2(position.x - aabbHalfWidth, position.y - aabbHalfWidth);
        Vector2 upperBound = new Vector2(position.x + aabbHalfWidth, position.y + aabbHalfWidth);

        // Implement the QueryCallback
        QueryCallback queryCallback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if (fixture.getFilterData().categoryBits == categoryBitsFinal) {
                    hasBody[0] = true;

                    return false;
                }

                // Return false to terminate the query early if you only need to check for the
                // presence of a body
                return true;
            }
        };

        // Perform the query
        world.QueryAABB(queryCallback, lowerBound.x, lowerBound.y, upperBound.x, upperBound.y);

        return hasBody[0];
    }

}

// public