package net.jasper.onlykeys.mixin.accessors;


import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionsScreen.class)
public interface OptionScreenAccessors {
    @Accessor("SKIN_CUSTOMIZATION_TEXT")
    Text getSKIN_CUSTOMIZATION_TEXT();
    @Accessor("SOUNDS_TEXT")
    Text getSOUNDS_TEXT();
    @Accessor("VIDEO_TEXT")
    Text getVIDEO_TEXT();
    @Accessor("CONTROL_TEXT")
    Text getCONTROL_TEXT();
    @Accessor("LANGUAGE_TEXT")
    Text getLANGUAGE_TEXT();
    @Accessor("CHAT_TEXT")
    Text getCHAT_TEXT();
    @Accessor("RESOURCE_PACK_TEXT")
    Text getRESOURCE_PACK_TEXT();
    @Accessor("ACCESSIBILITY_TEXT")
    Text getACCESSIBILITY_TEXT();
    @Accessor("TELEMETRY_TEXT")
    Text getTELEMETRY_TEXT();
    @Accessor("CREDITS_AND_ATTRIBUTION_TEXT")
    Text getCREDITS_AND_ATTRIBUTION_TEXT();
}
