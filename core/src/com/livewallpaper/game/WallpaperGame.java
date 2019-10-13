package com.livewallpaper.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.livewallpaper.game.gamemanager.GameManager;
import com.livewallpaper.game.screen.LoadingScreen;
import com.livewallpaper.game.screen.WallPaperScreen;

public class WallpaperGame extends Game {
    private WallPaperScreen wallPaperScreen;
    public GameManager gameManager;
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gameManager = new GameManager();
        this.setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {
        if (this.wallPaperScreen != null) {
            this.wallPaperScreen.dispose();
        }
        gameManager.dispose();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void resize(int w, int h) {
        if (this.wallPaperScreen != null) {
            if (w > h) {
                this.wallPaperScreen.setLandscape();
            } else {
                this.wallPaperScreen.setPortrait();
            }
        }
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void checkLoaded(float time) {
        boolean isLoadedResourceDone = gameManager.getAssetManager().update();
        if (isLoadedResourceDone) {
            gameManager.initTexture();
            wallPaperScreen = new WallPaperScreen(this);
            setScreen(this.wallPaperScreen);
        }
    }

}
