package com.zzhalex233.alexscaves.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.inventory.SpelunkeryTableContainer;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.CaveInfoItem;
import com.zzhalex233.alexscaves.server.message.SpelunkeryTableChangeMessage;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SpelunkeryTableGui extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/gui/spelunkery_table.png");
    private static final ResourceLocation TABLET_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/gui/spelunkery_table_tablet.png");
    private static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/gui/spelunkery_table_widgets.png");

    private final SpelunkeryTableContainer table;
    private final Random random = new Random();
    private final List<WordButton> wordButtons = new ArrayList<>();
    private ItemStack lastTablet = ItemStack.EMPTY;
    private ResourceLocation prevWordsFile;
    private WordButton targetWord;
    private int attemptsLeft;
    private int level;
    private int tickCount;
    private int highlightColor = 0XFFFFFF;
    private boolean finishedLevel;
    private boolean invalidTablet;
    private boolean draggingMagnify;
    private int magnifyX;
    private int magnifyY;
    private int lastMouseX;
    private int lastMouseY;
    private float passLevelProgress;

    public SpelunkeryTableGui(SpelunkeryTableContainer table, InventoryPlayer inventory) {
        super(table);
        this.table = table;
        this.xSize = 208;
        this.ySize = 256;
    }

    @Override
    public void initGui() {
        super.initGui();
        magnifyX = guiLeft + 170;
        magnifyY = guiTop + 130;
        for (WordButton button : wordButtons) {
            buttonList.add(button);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        tickCount++;
        ItemStack tablet = table.getSlot(0).getStack();
        if (!ItemStack.areItemStacksEqual(tablet, lastTablet)) {
            lastTablet = tablet.copy();
            invalidTablet = false;
            fullResetWords();
        }
        if (draggingMagnify) {
            magnifyX += clamp(lastMouseX - 19 - magnifyX, -15, 15);
            magnifyY += clamp(lastMouseY - 19 - magnifyY, -15, 15);
        } else {
            magnifyX += clamp(guiLeft + 170 - magnifyX, -20, 20);
            magnifyY += clamp(guiTop + 130 - magnifyY, -20, 20);
        }
        if (finishedLevel && passLevelProgress < 10.0F) {
            passLevelProgress += 0.5F;
        } else if (!finishedLevel && passLevelProgress > 0.0F) {
            passLevelProgress -= 0.5F;
        }
        boolean resetTabletFromWin = finishedLevel && passLevelProgress >= 10.0F && attemptsLeft > 0;
        if (!hasTablet()) {
            prevWordsFile = null;
            invalidTablet = false;
            clearWordWidgets();
        } else if (prevWordsFile == null || resetTabletFromWin) {
            prevWordsFile = getWordsForItem(tablet);
            if (prevWordsFile == null) {
                clearWordWidgets();
            } else {
                finishedLevel = false;
                passLevelProgress = 0.0F;
                generateWords(prevWordsFile);
            }
        }
        int color = table.getHighlightColor();
        if (color != -1) {
            highlightColor = color;
        }
        if (resetTabletFromWin && level >= 3) {
            level = 0;
            AlexsCaves.NETWORK_WRAPPER.sendToServer(new SpelunkeryTableChangeMessage(true));
            fullResetWords();
        } else if (finishedLevel && passLevelProgress >= 10.0F && attemptsLeft <= 0) {
            level = 0;
            AlexsCaves.NETWORK_WRAPPER.sendToServer(new SpelunkeryTableChangeMessage(false));
            fullResetWords();
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawMagnify();
        drawTabletText();
        drawDescText();
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("alexscaves.container.spelunkery_table");
        fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        mc.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        for (int bulb = 0; bulb < Math.min(level, 3); bulb++) {
            drawTexturedModalRect(guiLeft + 92 + bulb * 15, guiTop + 143, 0, 0, 13, 14);
        }
        if (hasPaper()) {
            drawTexturedModalRect(guiLeft - 80, guiTop + 10, 176, 0, 80, 149);
        }
        int tablet = hasTablet() ? attemptsLeft <= 1 ? 2 : 1 : 0;
        if (tablet > 0) {
            mc.getTextureManager().bindTexture(TABLET_TEXTURE);
            drawTexturedModalRect(guiLeft + 20, guiTop + 19, 0, (tablet - 1) * 121, 168, 120);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        if (!draggingMagnify && mouseX >= magnifyX && mouseX <= magnifyX + 38 && mouseY >= magnifyY && mouseY <= magnifyY + 38) {
            draggingMagnify = true;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingMagnify = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
        if (hasPaper() && hasTablet() && hasClickedAnyWord() && level < 3) {
            AlexsCaves.NETWORK_WRAPPER.sendToServer(new SpelunkeryTableChangeMessage(false));
        }
        super.onGuiClosed();
    }

    private void drawDescText() {
        int i = guiLeft - 58;
        int j = guiTop;
        if (invalidTablet) {
            String badTablet = I18n.format("alexscaves.container.spelunkery_table.bad_tablet");
            fontRenderer.drawString(badTablet, guiLeft + 105 - fontRenderer.getStringWidth(badTablet) / 2, j + 60, 0X000000);
        } else if (targetWord != null && hasTablet() && hasPaper()) {
            String find = I18n.format("alexscaves.container.spelunkery_table.find");
            String attempts = I18n.format("alexscaves.container.spelunkery_table.attempts");
            fontRenderer.drawString(find, i + 20 - fontRenderer.getStringWidth(find) / 2, j + 20, 0X99876C);
            fontRenderer.drawString(targetWord.normalText, i + 20 - fontRenderer.getStringWidth(targetWord.normalText) / 2, j + 35, highlightColor);
            fontRenderer.drawString(attempts, i + 20 - fontRenderer.getStringWidth(attempts) / 2, j + 60, 0X99876C);
            mc.getTextureManager().bindTexture(WIDGETS_TEXTURE);
            int tallySpace = 0;
            for (int tally = 1; tally <= attemptsLeft; tally++) {
                if (tally % 5 == 0) {
                    drawTexturedModalRect(i + 10 + tallySpace - 22, j + 70, 3, 52, 27, 14);
                    tallySpace += 7;
                } else {
                    drawTexturedModalRect(i + 10 + tallySpace, j + 70, 0, 52, 3, 14);
                    tallySpace += 4;
                }
            }
        }
    }

    private void drawTabletText() {
        if (hasTablet()) {
            for (WordButton button : wordButtons) {
                button.drawTranslationText();
            }
        }
    }

    private void drawMagnify() {
        mc.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        drawTexturedModalRect(magnifyX, magnifyY, 0, 14, 38, 38);
    }

    private boolean hasTablet() {
        return table.getSlot(0).getHasStack() && table.getSlot(0).getStack().getItem() == ACItemRegistry.CAVE_TABLET.item();
    }

    private boolean hasPaper() {
        return table.getSlot(1).getHasStack() && table.getSlot(1).getStack().getItem() == Items.PAPER;
    }

    private ResourceLocation getWordsForItem(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != ACItemRegistry.CAVE_TABLET.item()) {
            return null;
        }
        String lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase(java.util.Locale.ROOT);
        ResourceLocation resource = new ResourceLocation(AlexsCaves.MODID, "minigame/" + lang + "/" + getMinigameStr(stack) + ".txt");
        try {
            IResource loaded = mc.getResourceManager().getResource(resource);
            loaded.getInputStream().close();
        } catch (Exception exception) {
            resource = new ResourceLocation(AlexsCaves.MODID, "minigame/en_us/" + getMinigameStr(stack) + ".txt");
        }
        return resource;
    }

    private String getMinigameStr(ItemStack stack) {
        String caveBiome = CaveInfoItem.getCaveBiome(stack);
        return caveBiome == null || caveBiome.isEmpty() ? "magnetic_caves" : caveBiome.substring(caveBiome.indexOf(':') + 1);
    }

    private void fullResetWords() {
        clearWordWidgets();
        prevWordsFile = getWordsForItem(table.getSlot(0).getStack());
        if (prevWordsFile != null) {
            generateWords(prevWordsFile);
        }
    }

    private void clearWordWidgets() {
        buttonList.removeAll(wordButtons);
        wordButtons.clear();
        targetWord = null;
    }

    private void generateWords(ResourceLocation file) {
        clearWordWidgets();
        List<String> allWords;
        try {
            IResource resource = mc.getResourceManager().getResource(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            allWords = IOUtils.readLines(reader);
            reader.close();
        } catch (IOException exception) {
            allWords = new ArrayList<>();
            invalidTablet = true;
            AlexsCaves.LOGGER.error("Could not load spelunkery minigame file {}", file);
        }
        Collections.shuffle(allWords);
        int maxWidth = 160;
        int maxLines = 8;
        int wordLines = 0;
        int wordLineWidth = 0;
        while (wordLines < maxLines && !allWords.isEmpty()) {
            String text = allWords.remove(0).toUpperCase(java.util.Locale.ROOT);
            int wordWidth = text.length() * 6;
            if (wordLineWidth + wordWidth + 30 < maxWidth) {
                WordButton button = new WordButton(25 + wordLineWidth, 25 + 12 * wordLines, wordWidth, 12, text);
                wordButtons.add(button);
                buttonList.add(button);
                wordLineWidth += wordWidth;
            } else {
                wordLineWidth = 0;
                wordLines++;
            }
        }
        if (!wordButtons.isEmpty()) {
            targetWord = wordButtons.get(wordButtons.size() <= 1 ? 0 : random.nextInt(wordButtons.size()));
            attemptsLeft = 5;
        }
    }

    private void onClickWord(WordButton button) {
        if (finishedLevel) {
            return;
        }
        if (button == targetWord) {
            level++;
            playUi(level >= 3 ? ACSoundRegistry.SPELUNKERY_TABLE_SUCCESS_COMPLETE : ACSoundRegistry.SPELUNKERY_TABLE_SUCCESS);
            finishedLevel = true;
        } else {
            if (attemptsLeft > 0) {
                attemptsLeft--;
            }
            playUi(attemptsLeft <= 1 ? ACSoundRegistry.SPELUNKERY_TABLE_CRACK : ACSoundRegistry.SPELUNKERY_TABLE_ATTEMPT_FAIL);
            if (attemptsLeft <= 0) {
                finishedLevel = true;
            }
        }
    }

    private float getRevealWordsAmount() {
        return finishedLevel ? Math.min(passLevelProgress * 0.33F, 1.0F) : 0.0F;
    }

    private boolean hasClickedAnyWord() {
        for (WordButton button : wordButtons) {
            if (!button.enabled) {
                return true;
            }
        }
        return false;
    }

    private void playUi(net.minecraft.util.SoundEvent sound) {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private class WordButton extends GuiButton {
        private final String normalText;

        WordButton(int x, int y, int width, int height, String text) {
            super(wordButtons.size(), guiLeft + x, guiTop + y, width, height, text);
            this.normalText = text;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible || !hasTablet()) {
                return;
            }
            int reveal = (int) (getRevealWordsAmount() * 255);
            int hidden = 255 - reveal;
            if (hidden > 0) {
                drawEquidistant(enabled ? obfuscate(normalText) : normalText, x, y, (hidden << 24) | (enabled ? 0X404040 : 0XBFBFBF));
            }
            if (reveal > 0) {
                drawEquidistant(normalText, x, y, (reveal << 24) | (this == targetWord ? highlightColor : 0XBFBFBF));
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY) && hasPaper()) {
                onClickWord(this);
                enabled = false;
                return true;
            }
            return false;
        }

        void drawTranslationText() {
            if (!enabled && magnifyX + 32 >= x && magnifyX + 5 <= x + width && magnifyY + 32 >= y && magnifyY + 6 <= y + height) {
                int alpha = (int) ((Math.sin((tickCount + mc.getRenderPartialTicks()) * 0.2F) + 1.0F) * 127.5F);
                drawEquidistant(normalText, x, y, (Math.max(25, alpha) << 24) | highlightColor);
            }
        }

        private void drawEquidistant(String text, int x, int y, int color) {
            for (int i = 0; i < text.length(); i++) {
                fontRenderer.drawString(String.valueOf(text.charAt(i)), x + i * 6, y, color);
            }
        }

        private String obfuscate(String text) {
            String glyphs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            StringBuilder builder = new StringBuilder(text.length());
            for (int i = 0; i < text.length(); i++) {
                builder.append(glyphs.charAt(Math.abs(text.charAt(i) + tickCount + i) % glyphs.length()));
            }
            return builder.toString();
        }
    }
}
