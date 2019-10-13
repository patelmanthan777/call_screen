package com.livewallpaper.game.gamemanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {
    public static float V_WIDTH = 15f;
    public static float V_HEIGHT = 25f;
    public static float WINDOW_WIDTH = 480;
    public static float WINDOW_HEIGHT = 800;
    private String paths[] = {"Avenger/1.jpg", "Avenger/1.png", "Avenger/2.png", "Avenger/3.png"};
    private AssetManager assetManager;

    private Texture[] textures;


    public GameManager() {
        if (assetManager == null) {
            assetManager = new AssetManager();
        }

//        FileHandleResolver resolver = new ExternalFileHandleResolver();
//        assetManager.setLoader(Texture.class, new TextureLoader(resolver));
        float SCREEN_SCALE = Gdx.graphics.getWidth() / WINDOW_WIDTH;
        WINDOW_HEIGHT = Gdx.graphics.getHeight() / SCREEN_SCALE;
        V_HEIGHT = V_WIDTH * WINDOW_HEIGHT / WINDOW_WIDTH;
        loadResource();
    }

    public void loadResource() {
        for (int i = 0; i < paths.length; i++) {
            assetManager.load(paths[i], Texture.class);
        }
    }

    public void initTexture() {
        textures = new Texture[paths.length];
        for (int i = 0; i < paths.length; i++) {
            if (assetManager.isLoaded(paths[i])) {
                textures[i] = assetManager.get(paths[i], Texture.class);
            }
        }
    }

    public Texture[] getTextures() {
        return textures;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
