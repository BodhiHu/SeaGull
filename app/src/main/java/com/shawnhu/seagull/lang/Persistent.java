package com.shawnhu.seagull.lang;

/**
 * Created by shawnhu on 8/16/14.
 */
public interface Persistent {
    public void saveNow();
    public void restoreNow();
    public void saveValue(String key, String v);
    public String getSavedValue(String key);
}
