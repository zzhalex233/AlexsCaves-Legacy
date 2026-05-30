package com.zzhalex233.alexscaves.client.gui.book;

import java.io.IOException;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class CaveBookScreen extends GuiScreen {
    private static final String[] CATEGORIES = {
            "general",
            "resources",
            "mobs",
            "utilities",
            "secrets"
    };

    private final ItemStack book;
    private int page;

    public CaveBookScreen(ItemStack book) {
        this.book = book;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int y = height / 2 + 58;
        buttonList.add(new GuiButton(0, width / 2 - 92, y, 40, 20, "<"));
        buttonList.add(new GuiButton(1, width / 2 + 52, y, 40, 20, ">"));
    }

    @Override
    public void onGuiClosed() {
        if (mc.player != null) {
            mc.player.playSound(ACSoundRegistry.CAVE_BOOK_CLOSE, 0.7F, 1.0F);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 || button.id == 1) {
            page = (page + (button.id == 0 ? CATEGORIES.length - 1 : 1)) % CATEGORIES.length;
            if (mc.player != null) {
                mc.player.playSound(ACSoundRegistry.CAVE_BOOK_TURN, 0.6F, 1.0F);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int left = width / 2 - 110;
        int top = height / 2 - 82;
        drawRect(left, top, left + 220, top + 164, 0xFF2E211A);
        drawRect(left + 4, top + 4, left + 216, top + 160, 0xFFE9D5AA);
        drawCenteredString(fontRenderer, book.getDisplayName(), width / 2, top + 14, 0x3F2415);
        drawCenteredString(fontRenderer, I18n.format("item.alexscaves.cave_book." + CATEGORIES[page]), width / 2, top + 44, 0x5B321C);
        fontRenderer.drawSplitString(TextFormatting.DARK_GRAY + I18n.format("item.alexscaves.cave_book.desc"), left + 22, top + 72, 176, 0x4A3324);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
