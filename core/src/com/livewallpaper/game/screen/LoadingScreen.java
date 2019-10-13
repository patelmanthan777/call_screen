package com.livewallpaper.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.livewallpaper.game.WallpaperGame;


public class LoadingScreen implements Screen {
    private final float loadingDimen;
    private Texture loadingTexture;
    private final ShapeRenderer shapeRenderer;
    private WallpaperGame game;
    private float splashTime = 0;

    public LoadingScreen(WallpaperGame game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer(20); // 20 vertex seems to be enough for a rectangle
        loadingTexture = new Texture(Gdx.files.internal("gfx/ic_loading.png"));
        loadingDimen =
                0.2f * (Gdx.app.getGraphics().getWidth() < Gdx.app.getGraphics().getHeight() ?
                        Gdx.app.getGraphics().getWidth() : Gdx.app.getGraphics().getHeight());
        game.gameManager.loadResource();
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color color = new Color(Color.BLACK);
        color.a = 1.0f;
        shapeRenderer.setColor(color);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        game.getBatch().begin();
        game.getBatch().draw(loadingTexture,
                Gdx.app.getGraphics().getWidth() / 2 - loadingDimen / 2,
                Gdx.app.getGraphics().getHeight() / 2 - loadingDimen / 2,
                loadingDimen, loadingDimen);

        game.getBatch().end();

        splashTime += delta;
        game.checkLoaded(splashTime);


    }

    @Override
    public void resize(int width, int height) {

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
}
