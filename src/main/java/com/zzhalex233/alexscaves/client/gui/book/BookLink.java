package com.zzhalex233.alexscaves.client.gui.book;

public class BookLink {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String target;
    private final boolean enabled;

    public BookLink(int x, int y, int width, int height, String target, boolean enabled) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.target = target;
        this.enabled = enabled;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public String getTarget() {
        return target;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
