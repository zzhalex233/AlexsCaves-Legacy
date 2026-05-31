package com.zzhalex233.alexscaves.client.gui.book;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BookWidget {
    private final String type;
    private final int displayPage;
    private final int x;
    private final int y;
    private final float scale;
    private final String itemName;
    private final String imageName;
    private final String recipeName;
    private final String entityName;
    private final String entityNbt;
    private final String boxImage;
    private final int u0;
    private final int v0;
    private final int u1;
    private final int v1;
    private final int textureWidth;
    private final int textureHeight;
    private final float boxWidth;
    private final float boxHeight;
    private final float boxScale;
    private final float entityXOffset;
    private final float entityYOffset;
    private final float rotX;
    private final float rotY;
    private final float rotZ;
    private ItemStack itemStack = ItemStack.EMPTY;
    private IRecipe recipe;
    private Entity renderEntity;
    private boolean attemptedEntity;
    private List<ItemStack> jsonIngredients;
    private ItemStack jsonOutput = ItemStack.EMPTY;
    private boolean jsonSmelting;
    private boolean attemptedJsonRecipe;

    private BookWidget(JsonObject object) {
        this.type = JsonUtils.getString(object, "type", "");
        this.displayPage = JsonUtils.getInt(object, "display_page", 0);
        this.x = JsonUtils.getInt(object, "x", 0);
        this.y = JsonUtils.getInt(object, "y", 0);
        this.scale = JsonUtils.getFloat(object, "scale", 1.0F);
        this.itemName = JsonUtils.getString(object, "item", "");
        this.imageName = JsonUtils.getString(object, "image", "");
        this.recipeName = JsonUtils.getString(object, "recipe_id", "");
        this.entityName = JsonUtils.getString(object, "entity_id", "");
        this.entityNbt = JsonUtils.getString(object, "nbt", "");
        this.boxImage = JsonUtils.getString(object, "box_image", "");
        this.u0 = JsonUtils.getInt(object, "u0", 0);
        this.v0 = JsonUtils.getInt(object, "v0", 0);
        this.u1 = JsonUtils.getInt(object, "u1", 16);
        this.v1 = JsonUtils.getInt(object, "v1", 16);
        this.textureWidth = JsonUtils.getInt(object, "width", 256);
        this.textureHeight = JsonUtils.getInt(object, "height", 256);
        this.boxWidth = JsonUtils.getFloat(object, "box_width", 50.0F);
        this.boxHeight = JsonUtils.getFloat(object, "box_height", 35.0F);
        this.boxScale = JsonUtils.getFloat(object, "box_scale", 1.0F);
        this.entityXOffset = JsonUtils.getFloat(object, "entity_x_offset", 0.0F);
        this.entityYOffset = JsonUtils.getFloat(object, "entity_y_offset", 0.0F);
        this.rotX = JsonUtils.getFloat(object, "rot_x", 0.0F);
        this.rotY = JsonUtils.getFloat(object, "rot_y", 0.0F);
        this.rotZ = JsonUtils.getFloat(object, "rot_z", 0.0F);
    }

    public static BookWidget read(JsonObject object) {
        return new BookWidget(object);
    }

    public boolean shouldRender(int page) {
        return displayPage == page || displayPage == page + 1;
    }

    public void render(CaveBookScreen screen, int leftPageX, int rightPageX, int pageTop, int page) {
        int drawX = (displayPage == page ? leftPageX : rightPageX) + x;
        int drawY = pageTop + y;
        if ("item".equals(type)) {
            renderItem(drawX, drawY);
        } else if ("image".equals(type)) {
            renderImage(drawX, drawY);
        } else if ("crafting_recipe".equals(type)) {
            renderRecipe(drawX, drawY);
        } else if ("entity_box".equals(type)) {
            renderEntityBox(drawX, drawY);
        } else if ("entity".equals(type)) {
            renderEntity(drawX, drawY);
        }
    }

    private void renderItem(int drawX, int drawY) {
        ItemStack stack = getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(drawX, drawY, 200.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.renderItemAndEffectIntoGUI(stack, 0, 0);
        renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, stack, 0, 0, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void renderImage(int drawX, int drawY) {
        if (imageName.isEmpty()) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(imageName));
        GlStateManager.pushMatrix();
        GlStateManager.translate(drawX, drawY, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        Minecraft.getMinecraft().currentScreen.drawTexturedModalRect(0, 0, u0, v0, Math.max(1, u1 - u0), Math.max(1, v1 - v0));
        GlStateManager.popMatrix();
    }

    private void renderRecipe(int drawX, int drawY) {
        IRecipe recipe = getRecipe();
        List<ItemStack> ingredients = recipe == null ? getJsonIngredients() : getDisplayIngredients(recipe);
        ItemStack output = recipe == null ? jsonOutput : recipe.getRecipeOutput();
        if (output.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(drawX, drawY, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        drawRecipeFrame(jsonSmelting);
        for (int i = 0; i < ingredients.size() && i < 9; i++) {
            ItemStack stack = ingredients.get(i);
            if (!stack.isEmpty()) {
                renderSlotItem(stack, -33 + i % 3 * 18, -18 + i / 3 * 18);
            }
        }
        renderSlotItem(output, 57, 0);
        GlStateManager.popMatrix();
    }

    private void drawRecipeFrame(boolean smelting) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(AlexsCaves.MODID, smelting ? "textures/gui/book/smelting_grid.png" : "textures/gui/book/crafting_grid.png"));
        Minecraft.getMinecraft().currentScreen.drawTexturedModalRect(-42, -28, 0, 0, 82, 55);
    }

    private void renderSlotItem(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 200.0F);
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private ItemStack getItemStack() {
        if (itemStack.isEmpty() && !itemName.isEmpty()) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
            if (item != null) {
                itemStack = new ItemStack(item);
            }
        }
        return itemStack;
    }

    private IRecipe getRecipe() {
        if (recipe == null && !recipeName.isEmpty()) {
            recipe = ForgeRegistries.RECIPES.getValue(new ResourceLocation(recipeName));
        }
        return recipe;
    }

    private List<ItemStack> getJsonIngredients() {
        if (jsonIngredients == null) {
            jsonIngredients = new ArrayList<>();
        }
        if (!attemptedJsonRecipe) {
            attemptedJsonRecipe = true;
            readJsonRecipe();
        }
        return jsonIngredients;
    }

    private void readJsonRecipe() {
        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(AlexsCaves.MODID, "recipes/" + getRecipePathName() + ".json"));
            JsonObject object = new com.google.gson.JsonParser().parse(new java.io.InputStreamReader(resource.getInputStream(), java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            String type = JsonUtils.getString(object, "type", "");
            jsonSmelting = "minecraft:smelting".equals(type);
            if (jsonSmelting) {
                jsonIngredients.add(readStack(JsonUtils.getJsonObject(object, "ingredient")));
                jsonOutput = readResult(object.get("result"));
            } else if (object.has("pattern") && object.has("key")) {
                readShapedRecipe(object);
            } else if (object.has("ingredients")) {
                readShapelessRecipe(object);
            }
        } catch (Exception ignored) {
            jsonOutput = ItemStack.EMPTY;
        }
    }

    private String getRecipePathName() {
        ResourceLocation id = new ResourceLocation(recipeName);
        return id.getPath();
    }

    private void readShapedRecipe(JsonObject object) {
        JsonObject key = JsonUtils.getJsonObject(object, "key");
        JsonArray pattern = JsonUtils.getJsonArray(object, "pattern");
        for (JsonElement rowElement : pattern) {
            String row = rowElement.getAsString();
            for (int i = 0; i < 3; i++) {
                if (i < row.length() && row.charAt(i) != ' ') {
                    JsonElement ingredient = key.get(String.valueOf(row.charAt(i)));
                    jsonIngredients.add(ingredient == null ? ItemStack.EMPTY : readStack(ingredient.getAsJsonObject()));
                } else {
                    jsonIngredients.add(ItemStack.EMPTY);
                }
            }
        }
        jsonOutput = readResult(object.get("result"));
    }

    private void readShapelessRecipe(JsonObject object) {
        JsonArray ingredients = JsonUtils.getJsonArray(object, "ingredients");
        for (JsonElement ingredient : ingredients) {
            jsonIngredients.add(readStack(ingredient.getAsJsonObject()));
        }
        jsonOutput = readResult(object.get("result"));
    }

    private ItemStack readResult(JsonElement element) {
        if (element == null) {
            return ItemStack.EMPTY;
        }
        if (element.isJsonPrimitive()) {
            return getStack(element.getAsString(), 1);
        }
        JsonObject object = element.getAsJsonObject();
        return getStack(JsonUtils.getString(object, "item", ""), JsonUtils.getInt(object, "count", 1));
    }

    private ItemStack readStack(JsonObject object) {
        if (object.has("item")) {
            return getStack(JsonUtils.getString(object, "item"), JsonUtils.getInt(object, "count", 1));
        }
        if (object.has("tag")) {
            return getOreFallback(JsonUtils.getString(object, "tag"));
        }
        if (object.has("type")) {
            return getOreFallback(JsonUtils.getString(object, "ore", ""));
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getStack(String name, int count) {
        if (name == null || name.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    private ItemStack getOreFallback(String oreName) {
        if (oreName.contains("ingotIron")) {
            return new ItemStack(Items.IRON_INGOT);
        }
        if (oreName.contains("nuggetIron")) {
            return new ItemStack(Items.IRON_NUGGET);
        }
        if (oreName.contains("gemDiamond")) {
            return new ItemStack(Items.DIAMOND);
        }
        if (oreName.contains("stone")) {
            return new ItemStack(Blocks.STONE);
        }
        return ItemStack.EMPTY;
    }

    private List<ItemStack> getDisplayIngredients(IRecipe recipe) {
        NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
        List<ItemStack> stacks = new ArrayList<>();
        int width = recipe instanceof ShapedRecipes ? ((ShapedRecipes) recipe).recipeWidth : 3;
        for (int i = 0; i < recipeIngredients.size(); i++) {
            Ingredient ingredient = recipeIngredients.get(i);
            ItemStack[] matching = ingredient.getMatchingStacks();
            stacks.add(matching.length == 0 ? ItemStack.EMPTY : matching[(int) ((Minecraft.getSystemTime() / 1000L) % matching.length)]);
            if (width > 0 && i % width == width - 1 && width < 3) {
                for (int pad = width; pad < 3; pad++) {
                    stacks.add(ItemStack.EMPTY);
                }
            }
        }
        return stacks;
    }

    private void renderEntityBox(int drawX, int drawY) {
        if (!boxImage.isEmpty()) {
            drawEntityBorder(drawX, drawY);
        }
        renderEntity(drawX + Math.round(entityXOffset * 0.1F * scale), drawY + Math.round(entityYOffset * 0.1F * scale));
    }

    private void drawEntityBorder(int drawX, int drawY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(boxImage));
        int width = Math.max(1, Math.round(boxWidth * boxScale));
        int height = Math.max(1, Math.round(boxHeight * boxScale));
        int left = drawX - width / 2;
        int top = drawY - height / 2;
        Gui.drawScaledCustomSizeModalRect(left, top, 0.0F, 0.0F, 64, 64, width, height, 64.0F, 64.0F);
    }

    private void renderEntity(int drawX, int drawY) {
        Entity entity = getRenderEntity();
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase living = (EntityLivingBase) entity;
        float previousYaw = living.rotationYaw;
        float previousYawHead = living.rotationYawHead;
        float previousRenderYaw = living.renderYawOffset;
        float previousPitch = living.rotationPitch;
        living.rotationYaw = rotY;
        living.rotationYawHead = rotY;
        living.renderYawOffset = rotY;
        living.rotationPitch = rotX;
        int entityScale = Math.max(1, Math.round(100.0F * scale / Math.max(1.0F, Math.max(entity.width, entity.height) * 1.5F)));
        GuiInventory.drawEntityOnScreen(drawX, drawY + Math.round(20.0F * scale), entityScale, 0.0F, rotZ, living);
        living.rotationYaw = previousYaw;
        living.rotationYawHead = previousYawHead;
        living.renderYawOffset = previousRenderYaw;
        living.rotationPitch = previousPitch;
    }

    private Entity getRenderEntity() {
        if (!attemptedEntity) {
            attemptedEntity = true;
            if (!entityName.isEmpty() && Minecraft.getMinecraft().world != null) {
                renderEntity = EntityList.createEntityByIDFromName(new ResourceLocation(entityName), Minecraft.getMinecraft().world);
                if (renderEntity != null && !entityNbt.isEmpty()) {
                    try {
                        renderEntity.readFromNBT(JsonToNBT.getTagFromJson(entityNbt));
                    } catch (Exception ignored) {
                        renderEntity = null;
                    }
                }
            }
        }
        return renderEntity;
    }
}
