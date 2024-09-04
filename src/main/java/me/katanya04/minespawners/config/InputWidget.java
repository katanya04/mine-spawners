package me.katanya04.minespawners.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * Text input widget that represents a config value, renders with a title next to it
 */
@Environment(EnvType.CLIENT)
public class InputWidget extends TextFieldWidget {
    public final ConfigValue<?> configValue;
    private final TextRenderer textRenderer;
    public InputWidget(int x, int y, ConfigValue<?> configValue, TextRenderer textRenderer) {
        this(x, y, 50, 20, Text.of(configValue.key), configValue, textRenderer);
    }
    public InputWidget(int x, int y, int width, int height, Text message, ConfigValue<?> configValue, TextRenderer textRenderer) {
        super(textRenderer, x + textRenderer.getWidth(configValue.key + ": "), y, width, height, message);
        this.configValue = configValue;
        this.textRenderer = textRenderer;
        this.setMaxLength(32);
        this.setText(this.configValue.getValue().toString());
        this.setChangedListener(value -> {
            if (value != null && !value.isEmpty() && !value.isBlank())
                this.configValue.setValue(value);
        });
        this.setTooltip(Tooltip.of(Text.of(configValue.tooltip), Text.of(configValue.tooltip)));
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int titleWidth = textRenderer.getWidth(configValue.key + ": ");
        super.renderWidget(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, macroCaseToNormalCase(this.configValue.key) + ": ",
                getX() - titleWidth, getY() + height / 2 - textRenderer.fontHeight / 2, 0xFFFFFFFF, true);
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private static String macroCaseToNormalCase(String macroCase) {
        return macroCase.toLowerCase().replace('_', ' ')
                .replaceFirst(String.valueOf(Character.toLowerCase(macroCase.charAt(0))), String.valueOf(macroCase.charAt(0)));
    }
}
