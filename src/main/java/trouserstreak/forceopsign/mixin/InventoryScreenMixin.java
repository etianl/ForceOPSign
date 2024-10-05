//Credits to CrushedPixel for their first implementation of a forceOP sign module https://www.youtube.com/watch?v=KofDNaPZWfg

package trouserstreak.forceopsign.mixin;

import modes.CustomMenuScreen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

	@Unique
	private final MinecraftClient mc = MinecraftClient.getInstance();

	public InventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory inventory, Text text) {
		super(screenHandler, inventory, text);
	}

	@Inject(method = "init", at = @At("TAIL"))
	protected void init(CallbackInfo ci) {
		addDrawableChild(new ButtonWidget.Builder(Text.literal("ForceOPSign"), this::openCustomMenu)
				.position(x + 50, y + 165)
				.size(100, 20)
				.build()
		);
	}

	@Unique
	private void openCustomMenu(ButtonWidget button) {
		mc.setScreen(new CustomMenuScreen(this));
	}
}