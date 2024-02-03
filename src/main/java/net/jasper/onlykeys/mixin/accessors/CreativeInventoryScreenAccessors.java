package net.jasper.onlykeys.mixin.accessors;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(CreativeInventoryScreen.class)
public interface CreativeInventoryScreenAccessors {
    @Accessor("searchBox")
    TextFieldWidget getSearchBox();

    @Accessor("selectedTab")
    static void setSelectedTab(ItemGroup itemGroup) {
        throw new NotImplementedException("CreativeInventoryScreen Mixin failed to apply");
    }

    @Invoker("refreshSelectedTab")
    void refreshSelectedTabInvoker(Collection<ItemStack> displayStacks);
}
