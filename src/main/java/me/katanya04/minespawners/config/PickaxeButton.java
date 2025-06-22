package me.katanya04.minespawners.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * A square button with an image of a pickaxe, upon clicking toggles the blacklisted state of that pickaxe
 */
@Environment(EnvType.CLIENT)
public class PickaxeButton extends ButtonWidget {
    public final Item pickaxe;
    public final Screen screen;
    public PickaxeButton(int x, int y, int size, Item pickaxe, Screen screen) {
        super(x, y, size, size, Text.empty(),
                self -> {
                    List<String> blacklistedPickaxes = SimpleConfig.BLACKLISTED_PICKAXES.getValue();
                    if (blacklistedPickaxes.contains(pickaxe.toString())) {
                        blacklistedPickaxes.remove(pickaxe.toString());
                    } else {
                        blacklistedPickaxes.add(pickaxe.toString());
                    }
                    SimpleConfig.BLACKLISTED_PICKAXES.setValue(blacklistedPickaxes);
                },
                supplier ->
                        MutableText.of(new PlainTextContent.Literal(pickaxe.getName().getString()))
        );
        this.pickaxe = pickaxe;
        this.screen = screen;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        int itemX = this.getX() + (this.width - 16) / 2;
        int itemY = this.getY() + (this.height - 16) / 2;
        context.drawItem(this.pickaxe.getDefaultStack(), itemX, itemY);
        if (this.isHovered())
            context.drawTooltip(this.screen.getTextRenderer(), this.pickaxe.getName(), mouseX, mouseY);
        if (SimpleConfig.BLACKLISTED_PICKAXES.contains(pickaxe.toString())) {
            context.drawText(this.screen.getTextRenderer(),
                    MutableText.of(new PlainTextContent.Literal("X")).formatted(Formatting.BOLD),
                    getX(), getY(), 0xFFFF0000, true);
        }
    }
}
