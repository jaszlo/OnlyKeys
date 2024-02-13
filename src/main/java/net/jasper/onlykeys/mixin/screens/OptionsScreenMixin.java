package net.jasper.onlykeys.mixin.screens;


import net.jasper.onlykeys.mixin.accessors.OptionScreenAccessors;
import net.jasper.onlykeys.mixin.accessors.ScreenAccessor;
import net.jasper.onlykeys.mod.OnlyKeysModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin {

    @Shadow @Final
    private Screen parent;


    @Shadow
    private ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier) {
        // Should not be called
        throw new NotImplementedException("Failed to shadow createButton");
    }

    @Shadow
    private void refreshResourcePacks(ResourcePackManager resourcePackManager) {
        // Should not be called
        throw new NotImplementedException("Failed to shadow refreshResourcePacks");
    }

    @Shadow
    private Widget createTopRightButton() {
        // Should not be called
        throw new NotImplementedException("Failed to shadow createTopRightButton");
    }

    @Inject(method="init", at=@At("HEAD"), cancellable=true)
    private void injected(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptions settings = client.options;
        OptionsScreen thisAsScreen = (OptionsScreen) (Object) this;
        OptionScreenAccessors optionScreenAccessors = (OptionScreenAccessors) this;

        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);
        adder.add(settings.getFov().createWidget(client.options, 0, 0, 150));
        adder.add(this.createTopRightButton());
        adder.add(EmptyWidget.ofHeight(26), 2);
        adder.add(this.createButton(optionScreenAccessors.getSKIN_CUSTOMIZATION_TEXT(), () -> new SkinOptionsScreen(thisAsScreen, settings)));
        adder.add(this.createButton(optionScreenAccessors.getSOUNDS_TEXT(), () -> new SoundOptionsScreen(thisAsScreen, settings)));
        adder.add(this.createButton(optionScreenAccessors.getVIDEO_TEXT(), () -> new VideoOptionsScreen(thisAsScreen, settings)));
        adder.add(this.createButton(optionScreenAccessors.getCONTROL_TEXT(), () -> new ControlsOptionsScreen(thisAsScreen, settings)));
        adder.add(this.createButton(optionScreenAccessors.getLANGUAGE_TEXT(), () -> new LanguageOptionsScreen(thisAsScreen, settings, client.getLanguageManager())));
        adder.add(this.createButton(optionScreenAccessors.getCHAT_TEXT(), () -> new ChatOptionsScreen(thisAsScreen, settings)));
        adder.add(this.createButton(optionScreenAccessors.getRESOURCE_PACK_TEXT(), () -> new PackScreen(client.getResourcePackManager(), this::refreshResourcePacks, client.getResourcePackDir(), Text.translatable("resourcePack.title"))));
        adder.add(this.createButton(optionScreenAccessors.getACCESSIBILITY_TEXT(), () -> new AccessibilityOptionsScreen(thisAsScreen, settings)));
        adder.add(this.createButton(optionScreenAccessors.getTELEMETRY_TEXT(), () -> new TelemetryInfoScreen(thisAsScreen, settings)));
        adder.add(this.createButton(optionScreenAccessors.getCREDITS_AND_ATTRIBUTION_TEXT(), () -> new CreditsAndAttributionScreen(thisAsScreen)));
        adder.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> client.setScreen(this.parent)).width(200).build(), 2, adder.copyPositioner().marginTop(6));
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, thisAsScreen.height / 6 - 12, thisAsScreen.width, thisAsScreen.height, 0.5F, 0.0F);

        ScreenAccessor screenAccessor = (ScreenAccessor) this;
        gridWidget.forEachChild(screenAccessor::addDrawableChildInvoker);
        OnlyKeysModClient.LOGGER.info("OVERWROTE OPTIONS SETTING INIT");
        ci.cancel();
    }


}
