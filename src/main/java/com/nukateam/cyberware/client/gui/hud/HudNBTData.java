package com.nukateam.cyberware.client.gui.hud;

import net.minecraft.nbt.CompoundTag;
import com.nukateam.cyberware.api.hud.IHudSaveData;

public class HudNBTData implements IHudSaveData {
    private CompoundTag tag;

    public HudNBTData(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public void setString(String key, String s) {
        tag.setString(key, s);
    }

    @Override
    public String getString(String key) {
        return tag.getString(key);
    }

    @Override
    public void setBoolean(String key, boolean b) {
        tag.setBoolean(key, b);
    }

    @Override
    public boolean getBoolean(String key) {
        return tag.getBoolean(key);
    }

    @Override
    public void setFloat(String key, float f) {
        tag.setFloat(key, f);
    }

    @Override
    public float getFloat(String key) {
        return tag.getFloat(key);
    }

    @Override
    public void setInteger(String key, int i) {
        tag.setInteger(key, i);
    }

    @Override
    public int getInteger(String key) {
        return tag.getInteger(key);
    }

    public CompoundTag getTag() {
        return tag;
    }

}
