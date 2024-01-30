package net.jasper.onlykeys.mixin.mouse;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mouse.class)
public interface MouseAccessors {
    @Accessor("x")
    void setX(double x);

    @Accessor("y")
    void setY(double y);
    @Invoker("onMouseButton")
    void onMouseButtonInvoker(long window, int button, int action, int mods);
}
