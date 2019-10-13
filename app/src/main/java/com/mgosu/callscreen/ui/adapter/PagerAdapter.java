package com.mgosu.callscreen.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mgosu.callscreen.data.api.wallpaper.image.tabLayout.ObjList;
import com.mgosu.callscreen.ui.fragment.FragmentCallFlash;
import com.mgosu.callscreen.ui.fragment.FragmentFavorite;
import com.mgosu.callscreen.ui.fragment.FragmentImage;
import com.mgosu.callscreen.ui.fragment.FragmentImage3d;
import com.mgosu.callscreen.ui.fragment.FragmentImageTab;
import com.mgosu.callscreen.ui.fragment.FragmentPopular;
import com.mgosu.callscreen.ui.fragment.FragmentVideo;
import com.mgosu.callscreen.ui.fragment.FragmentVideoTab;

import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private int numberTab;
    private boolean isNavi, isVideo;
    private boolean isImageWallpaper;
    private List<ObjList> objLists;

    public PagerAdapter(FragmentManager fm, int numberTab, boolean isNavi) {
        super(fm);
        this.isImageWallpaper = false;
        this.numberTab = numberTab;
        this.isNavi = isNavi;
        this.isImageWallpaper = false;
        this.isVideo = false;
    }

    public PagerAdapter(FragmentManager fm, int numberTab, List<ObjList> objLists, boolean isImageWallpaper, boolean isVideo) {
        super(fm);
        this.numberTab = numberTab;
        this.isNavi = false;
        this.isImageWallpaper = isImageWallpaper;
        this.objLists = objLists;
        this.isVideo = isVideo;
    }

    @Override
    public Fragment getItem(int i) {
        if (!isImageWallpaper) {
            switch (i) {
                case 0:
                    if (!isNavi)
                        return new FragmentCallFlash();
                    else return new FragmentImage3d();
                case 1:
                    if (!isNavi)
                        return new FragmentPopular();
                    else return new FragmentVideo();
                case 2:
                    if (!isNavi)
                        return FragmentFavorite.newInstance(false) ;
                    else return new FragmentImage();
                case 3:
                    if (!isNavi)
                        return null;
                    else return FragmentFavorite.newInstance(true);
                default:
                    return null;
            }
        } else {
            if (!isVideo)
                return FragmentImageTab.newInstance(objLists.get(i).getCatId());
            else return FragmentVideoTab.newInstance(objLists.get(i).getCatId());
        }

    }

    @Override
    public int getCount() {
        return numberTab;
    }
}
