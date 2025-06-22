package me.katanya04.minespawners.config;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PickaxesList extends ElementListWidget<PickaxesList.Entry> {
    public static final int BUTTON_SIZE = 20;
    public static final int BUTTON_MARGIN = 10;
    private final ConfigScreen configScreen;

    public PickaxesList(ConfigScreen configScreen, MinecraftClient minecraft, ClickableWidget slider) {
        super(  minecraft,
                configScreen.width,
                configScreen.height - (slider.y + slider.getHeight()),
                slider.y + slider.getHeight() + 10,
                configScreen.height - 37,
                BUTTON_SIZE + BUTTON_MARGIN
        );
        this.configScreen = configScreen;
        setEntries();
    }

    protected void setEntries() {
        this.clearEntries();
        this.addEntry(new TitleEntry(new TranslatableText("config.blacklisted_pickaxes")));
        int initialX = (getRowWidth() - getButtonsPerRow() * (BUTTON_MARGIN + BUTTON_SIZE) + BUTTON_MARGIN) / 2 + 25;
        for (int i = 0; i < configScreen.pickaxes.size(); i += getButtonsPerRow()) {
            this.addEntry(new RowEntry(configScreen.pickaxes.subList(i, Math.min(i + getButtonsPerRow(), configScreen.pickaxes.size())), initialX));
        }
    }

    @Override
    public int getRowWidth() {
        return configScreen.width - 50;
    }

    public int getButtonsPerRow() {
        return getRowWidth() / (BUTTON_SIZE + BUTTON_MARGIN);
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ElementListWidget.Entry<Entry> {}

    @Environment(EnvType.CLIENT)
    public class RowEntry extends Entry {
        private final PickaxeButton[] buttons;
        
        public RowEntry(List<Item> pickaxes, int initialX) {
            this.buttons = new PickaxeButton[pickaxes.size()];
            int x = initialX, i = 0;
            for (Item pickaxe : pickaxes) {
                buttons[i++] = new PickaxeButton(x, 0, BUTTON_SIZE, pickaxe, PickaxesList.this.configScreen, PickaxesList.this.client);
                x += BUTTON_SIZE + BUTTON_MARGIN;
            }
        }

        @Override
        public @NotNull List<? extends Selectable> selectableChildren() {
            return Arrays.asList(this.buttons);
        }

        @Override
        public void render(
                MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
        ) {
            Arrays.stream(this.buttons).forEach(b -> {
                b.y = y;
                b.render(matrices, mouseX, mouseY, tickDelta);
            });
        }

        @Override
        public @NotNull List<? extends Element> children() {
            return Arrays.asList(this.buttons);
        }
    }

    @Environment(EnvType.CLIENT)
    public class TitleEntry extends Entry {
        final Text title;
        private final int width;

        public TitleEntry(final Text title) {
            this.title = title;
            this.width = PickaxesList.this.client.textRenderer.getWidth(this.title);
        }

        @Override
        public @NotNull List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(new Selectable() {
                @Override
                public Selectable.SelectionType getType() {
                    return Selectable.SelectionType.HOVERED;
                }

                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    builder.put(NarrationPart.TITLE, TitleEntry.this.title);
                }
            });
        }

        @Override
        public void render(
                MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
        ) {
            PickaxesList.this.client.textRenderer.draw(
                    matrices, this.title, (float) PickaxesList.this.width / 2 - (float) this.width / 2,
                    y + (float) entryHeight / 2 - (float) PickaxesList.this.client.textRenderer.fontHeight / 2, -1
            );
        }

        @Override
        public @NotNull List<? extends Element> children() {
            return List.of();
        }
    }
}
