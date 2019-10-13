package com.livewallpaper.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.livewallpaper.game.WallpaperGame;
import com.livewallpaper.game.gamemanager.GameManager;

import sun.security.ssl.Debug;


public class WallPaperScreen implements Screen {
    private float scale = 1.2f;
    private Camera camera;
    private Viewport viewport;
    private WallpaperGame game;
    private Sprite sprites[];
    private float[] olds;

    public WallPaperScreen(WallpaperGame game) {
        this.game = game;
        sprites = new Sprite[game.gameManager.getTextures().length];
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite();
            sprites[i].setRegion(game.gameManager.getTextures()[i]);
            sprites[i].setBounds(-scale * (GameManager.V_HEIGHT / 2 - GameManager.V_WIDTH / 2),
                    -(scale - 1) * GameManager.V_HEIGHT / 2,
                    scale * GameManager.V_HEIGHT,
                    scale * GameManager.V_HEIGHT);
        }
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameManager.V_WIDTH, GameManager.V_HEIGHT);
        viewport.setCamera(camera);
        camera.position.set(GameManager.V_WIDTH / 2, GameManager.V_HEIGHT / 2, 0);
        olds = new float[2];
        olds[0] = 0;
        olds[1] = 0;

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].draw(game.getBatch());
        }
        game.getBatch().end();
        update(delta);
    }

    public void update(float delta) {
        boolean available = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
        if (available) {

            float[] accels = new float[2];
            accels[0] = Gdx.input.getAccelerometerX();
            accels[1] = Gdx.input.getAccelerometerY();
            float[] temp = new float[2];
            temp[0] = olds[0];
            temp[1] = olds[1];
            accels = lowPass(accels, temp);
            for (int i = 0; i < sprites.length; i++) {
                sprites[i].setX(sprites[i].getX() - (accels[0] - olds[0]) / (5f * (i*0.4f + 1)));
                sprites[i].setY(sprites[i].getY() - (accels[1] - olds[1]) / (5f * (i*0.4f  + 1)));
            }
            olds[0] = accels[0];
            olds[1] = accels[1];

        }

        camera.update();
    }

    private final float ALPHA = 0.25f;

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void setPortrait() {
        Gdx.graphics.getGL20().glClearColor(0.5f, 0, 0, 1);
    }

    public void setLandscape() {
        Gdx.graphics.getGL20().glClearColor(0, 0, 0.5f, 1);
    }
}
