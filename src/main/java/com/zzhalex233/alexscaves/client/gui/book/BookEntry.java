package com.zzhalex233.alexscaves.client.gui.book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.misc.CaveBookProgress;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class BookEntry {
    private static final Pattern LINK_PATTERN = Pattern.compile("\\{([^|{}]+)\\|([^{}]+)}");

    private final String titleKey;
    private final String parent;
    private final String textFile;
    @Nullable
    private final String requiredProgression;
    private final List<BookLine> lines = new ArrayList<>();
    private final List<BookWidget> widgets = new ArrayList<>();

    private BookEntry(String titleKey, String parent, String textFile, @Nullable String requiredProgression) {
        this.titleKey = titleKey;
        this.parent = parent;
        this.textFile = textFile;
        this.requiredProgression = requiredProgression;
    }

    public static BookEntry read(ResourceLocation location, CaveBookScreen screen) throws IOException {
        BookEntry entry = readMetadata(location);
        entry.loadText(screen);
        return entry;
    }

    public static BookEntry readMetadata(ResourceLocation location) throws IOException {
        IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(location);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
            BookEntry entry = new BookEntry(getString(object, "title"), getString(object, "parent"), getString(object, "text"), object.has("required_progression") ? getString(object, "required_progression") : null);
            if (object.has("widgets")) {
                JsonArray widgets = object.getAsJsonArray("widgets");
                for (JsonElement element : widgets) {
                    if (element.isJsonObject()) {
                        entry.widgets.add(BookWidget.read(element.getAsJsonObject()));
                    }
                }
            }
            return entry;
        }
    }

    private static String getString(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsString() : "";
    }

    private void loadText(CaveBookScreen screen) throws IOException {
        lines.clear();
        ResourceLocation text = getTextResource(screen.getLanguageCode());
        if (!resourceExists(text)) {
            text = getTextResource("en_us");
        }
        IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(text);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String raw;
            while ((raw = reader.readLine()) != null) {
                appendWrappedLine(screen, raw);
            }
        }
    }

    private ResourceLocation getTextResource(String language) {
        return new ResourceLocation(AlexsCaves.MODID, "books/" + language + "/" + textFile);
    }

    private boolean resourceExists(ResourceLocation location) {
        try {
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            stream.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private void appendWrappedLine(CaveBookScreen screen, String raw) {
        List<BookSegment> segments = parseSegments(screen, raw);
        if (segments.isEmpty()) {
            lines.add(new BookLine());
            return;
        }
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        BookLine line = new BookLine();
        for (BookSegment segment : segments) {
            String remaining = segment.text;
            while (!remaining.isEmpty()) {
                int room = CaveBookScreen.PAGE_TEXT_WIDTH - line.getWidth(font);
                int fit = getFittingLength(font, remaining, room);
                if (fit <= 0) {
                    lines.add(line);
                    line = new BookLine();
                    if (remaining.startsWith(" ")) {
                        remaining = remaining.substring(1);
                    }
                } else {
                    String part = remaining.substring(0, fit);
                    line.segments.add(new BookSegment(part, segment.target, segment.enabled));
                    remaining = remaining.substring(fit);
                    if (!remaining.isEmpty()) {
                        lines.add(line);
                        line = new BookLine();
                        if (remaining.startsWith(" ")) {
                            remaining = remaining.substring(1);
                        }
                    }
                }
            }
        }
        lines.add(line);
    }

    private List<BookSegment> parseSegments(CaveBookScreen screen, String raw) {
        List<BookSegment> segments = new ArrayList<>();
        Matcher matcher = LINK_PATTERN.matcher(raw);
        int cursor = 0;
        while (matcher.find()) {
            if (matcher.start() > cursor) {
                segments.add(new BookSegment(raw.substring(cursor, matcher.start()), null, false));
            }
            String display = matcher.group(1);
            String target = matcher.group(2);
            int visibility = screen.getEntryVisibility(target);
            if (visibility != 2) {
                segments.add(new BookSegment(visibility == 0 ? display : "???", target, visibility == 0));
            }
            cursor = matcher.end();
        }
        if (cursor < raw.length()) {
            segments.add(new BookSegment(raw.substring(cursor), null, false));
        }
        return segments;
    }

    private int getFittingLength(FontRenderer font, String text, int maxWidth) {
        if (font.getStringWidth(text) <= maxWidth) {
            return text.length();
        }
        int lastSpace = -1;
        for (int i = 1; i <= text.length(); i++) {
            if (text.charAt(i - 1) == ' ') {
                lastSpace = i;
            }
            if (font.getStringWidth(text.substring(0, i)) > maxWidth) {
                return lastSpace > 0 ? lastSpace : Math.max(1, i - 1);
            }
        }
        return text.length();
    }

    public int getVisibility(CaveBookScreen screen) {
        if (requiredProgression == null || requiredProgression.isEmpty() || Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.isCreative()) {
            return 0;
        }
        CaveBookProgress progress = screen.getCaveBookProgress();
        if (progress.isUnlockedFor(requiredProgression)) {
            return 0;
        }
        return progress.getSubcategoryFromPage(requiredProgression) == CaveBookProgress.Subcategory.SECRETS ? 2 : 1;
    }

    public String getTitle() {
        return I18n.format(titleKey);
    }

    public String getParent() {
        return parent;
    }

    public int getPageCount() {
        return Math.max(1, (int) Math.ceil(lines.size() / (float) CaveBookScreen.PAGE_LINES));
    }

    public List<BookLine> getLines() {
        return lines;
    }

    public List<BookWidget> getWidgets() {
        return widgets;
    }

    public static class BookLine {
        private final List<BookSegment> segments = new ArrayList<>();

        private int getWidth(FontRenderer font) {
            int width = 0;
            for (BookSegment segment : segments) {
                width += font.getStringWidth(segment.text);
            }
            return width;
        }

        public List<BookSegment> getSegments() {
            return segments;
        }
    }

    public static class BookSegment {
        private final String text;
        @Nullable
        private final String target;
        private final boolean enabled;

        private BookSegment(String text, @Nullable String target, boolean enabled) {
            this.text = text;
            this.target = target;
            this.enabled = enabled;
        }

        public String getText() {
            return text;
        }

        @Nullable
        public String getTarget() {
            return target;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}
