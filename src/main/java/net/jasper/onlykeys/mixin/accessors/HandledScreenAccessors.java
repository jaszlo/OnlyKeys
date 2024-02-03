package net.jasper.onlykeys.mixin.accessors;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessors {

    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();

}
