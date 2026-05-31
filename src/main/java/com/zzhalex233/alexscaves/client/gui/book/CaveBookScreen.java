package com.zzhalex233.alexscaves.client.gui.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.misc.CaveBookProgress;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CaveBookScreen extends GuiScreen {
    public static final int PAGE_LINES = 12;
    public static final int PAGE_TEXT_WIDTH = 92;
    private static final int TEXT_COLOR = 0x4A3324;
    private static final int LINK_COLOR = 0x1D4F8F;
    private static final int LINK_HOVER_COLOR = 0x2A8CDB;
    private static final int LINK_LOCKED_COLOR = 0x9B8B71;

    private final ItemStack book;
    private final CaveBookProgress caveBookProgress;
    private final List<ResourceLocation> history = new ArrayList<>();
    private final List<BookLink> links = new ArrayList<>();
    private BookEntry currentEntry;
    private ResourceLocation currentEntryLocation = new ResourceLocation(AlexsCaves.MODID, "books/root.json");
    private int page;
    private String loadError;
    private boolean hoveringLockedEntry;

    public CaveBookScreen(ItemStack book) {
        this.book = book;
        this.caveBookProgress = CaveBookProgress.getCaveBookProgress(net.minecraft.client.Minecraft.getMinecraft().player);
    }

    @Override
    public void initGui() {
        buttonList.clear();
        loadEntry(currentEntryLocation, false);
        int y = height / 2 + 76;
        buttonList.add(new GuiButton(0, width / 2 - 116, y, 42, 20, "<"));
        buttonList.add(new GuiButton(1, width / 2 + 74, y, 42, 20, ">"));
        buttonList.add(new GuiButton(2, width / 2 - 22, y, 44, 20, I18n.format("gui.back")));
    }

    @Override
    public void onGuiClosed() {
        if (mc.player != null) {
            mc.player.playSound(ACSoundRegistry.CAVE_BOOK_CLOSE, 0.7F, 1.0F);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && page > 0) {
            page = Math.max(0, page - 2);
            if (mc.player != null) {
                mc.player.playSound(ACSoundRegistry.CAVE_BOOK_TURN, 0.6F, 1.0F);
            }
        } else if (button.id == 1 && currentEntry != null && page + 2 < currentEntry.getPageCount()) {
            page += 2;
            if (mc.player != null) {
                mc.player.playSound(ACSoundRegistry.CAVE_BOOK_TURN, 0.6F, 1.0F);
            }
        } else if (button.id == 2) {
            goBack();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        links.clear();
        hoveringLockedEntry = false;
        int left = width / 2 - 138;
        int top = height / 2 - 98;
        drawRect(left, top, left + 276, top + 190, 0xFF2E211A);
        drawRect(left + 5, top + 5, left + 136, top + 176, 0xFFE9D5AA);
        drawRect(left + 140, top + 5, left + 271, top + 176, 0xFFE9D5AA);
        drawCenteredString(fontRenderer, currentEntry == null ? book.getDisplayName() : currentEntry.getTitle(), width / 2, top + 14, 0x3F2415);
        if (currentEntry == null) {
            drawCenteredString(fontRenderer, loadError == null ? I18n.format("book.alexscaves.root") : loadError, width / 2, top + 84, TEXT_COLOR);
        } else {
            drawPage(left + 22, top + 34, page, mouseX, mouseY);
            drawPage(left + 157, top + 34, page + 1, mouseX, mouseY);
            drawWidgets(left + 22, left + 157, top + 34);
            drawCenteredString(fontRenderer, (page + 1) + " / " + currentEntry.getPageCount(), width / 2, top + 158, 0x6A5032);
        }
        updateButtons();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (hoveringLockedEntry) {
            drawHoveringText(Arrays.asList(I18n.format("book.alexscaves.page_locked_0"), I18n.format("book.alexscaves.page_locked_1")), mouseX, mouseY);
        }
    }

    private void drawWidgets(int leftPageX, int rightPageX, int pageTop) {
        for (BookWidget widget : currentEntry.getWidgets()) {
            if (widget.shouldRender(page)) {
                widget.render(this, leftPageX, rightPageX, pageTop, page);
            }
        }
    }

    private void drawPage(int x, int y, int pageIndex, int mouseX, int mouseY) {
        int firstLine = pageIndex * PAGE_LINES;
        List<BookEntry.BookLine> lines = currentEntry.getLines();
        for (int i = 0; i < PAGE_LINES && firstLine + i < lines.size(); i++) {
            int drawX = x;
            int drawY = y + i * 10;
            for (BookEntry.BookSegment segment : lines.get(firstLine + i).getSegments()) {
                String text = segment.getText();
                int color = TEXT_COLOR;
                if (segment.getTarget() != null) {
                    BookLink link = new BookLink(drawX, drawY, fontRenderer.getStringWidth(text), 9, segment.getTarget(), segment.isEnabled());
                    boolean hovered = link.isMouseOver(mouseX, mouseY);
                    if (segment.isEnabled()) {
                        links.add(link);
                        color = hovered ? LINK_HOVER_COLOR : LINK_COLOR;
                    } else {
                        links.add(link);
                        hoveringLockedEntry |= hovered;
                        color = LINK_LOCKED_COLOR;
                    }
                }
                fontRenderer.drawString(text, drawX, drawY, color);
                drawX += fontRenderer.getStringWidth(text);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (BookLink link : links) {
            if (link.isEnabled() && link.isMouseOver(mouseX, mouseY)) {
                loadEntry(new ResourceLocation(AlexsCaves.MODID, "books/" + link.getTarget()), true);
                if (mc.player != null) {
                    mc.player.playSound(ACSoundRegistry.CAVE_BOOK_TURN, 0.6F, 1.0F);
                }
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void loadEntry(ResourceLocation location, boolean pushHistory) {
        try {
            BookEntry entry = BookEntry.read(location, this);
            if (pushHistory && currentEntry != null) {
                history.add(currentEntryLocation);
            }
            currentEntry = entry;
            currentEntryLocation = location;
            page = 0;
            loadError = null;
        } catch (Exception ex) {
            AlexsCaves.LOGGER.warn("Failed to load cave book entry {}", location, ex);
            currentEntry = null;
            loadError = location.toString();
        }
    }

    private void goBack() {
        if (!history.isEmpty()) {
            ResourceLocation previous = history.remove(history.size() - 1);
            loadEntry(previous, false);
        } else if (currentEntry != null && currentEntry.getParent() != null && !currentEntry.getParent().isEmpty()) {
            loadEntry(new ResourceLocation(AlexsCaves.MODID, "books/" + currentEntry.getParent()), false);
        }
    }

    private void updateButtons() {
        if (buttonList.size() >= 3) {
            buttonList.get(0).enabled = currentEntry != null && page > 0;
            buttonList.get(1).enabled = currentEntry != null && page + 2 < currentEntry.getPageCount();
            buttonList.get(2).enabled = currentEntry != null && (!history.isEmpty() || currentEntry.getParent() != null && !currentEntry.getParent().isEmpty());
        }
    }

    public int getEntryVisibility(String linkTo) {
        try {
            return BookEntry.readMetadata(new ResourceLocation(AlexsCaves.MODID, "books/" + linkTo)).getVisibility(this);
        } catch (Exception ex) {
            return 2;
        }
    }

    public CaveBookProgress getCaveBookProgress() {
        return caveBookProgress;
    }

    public String getLanguageCode() {
        Language language = mc.getLanguageManager().getCurrentLanguage();
        return language == null ? "en_us" : language.getLanguageCode().toLowerCase();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
