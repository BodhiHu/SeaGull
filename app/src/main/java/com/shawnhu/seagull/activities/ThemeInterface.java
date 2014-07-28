package com.shawnhu.seagull.activities;

/**
 * Created by shawnhu on 7/28/14.
 */
public interface ThemeInterface {
    /*
     * Caution: should be called before any views are instantiated in the context.
     */
    public void applyTheme(int themeResId);
}
