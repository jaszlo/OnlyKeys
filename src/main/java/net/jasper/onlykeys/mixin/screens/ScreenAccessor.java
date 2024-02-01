package net.jasper.onlykeys.mixin.screens;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Invoker("addDrawableChild")
    @SuppressWarnings("UnusedReturnValue")
    <T extends Element & Drawable & Selectable> T addDrawableChildInvoker(T drawableElement);

}
