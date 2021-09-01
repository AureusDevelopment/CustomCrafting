package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class MenuResult extends CCWindow {

    public MenuResult(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "result", 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new ButtonContainerItemResult(i));
        }
        registerButton(new ActionButton<>("back", new ButtonState<>(ClusterMain.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>("tags", new ButtonState<>(ClusterRecipeCreator.TAGS, Material.NAME_TAG, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeCreatorCache().getTagSettingsCache().setRecipeItemStack(cache.getRecipeCreatorCache().getRecipeCache().getResult());
            guiHandler.openWindow("tag_settings");
            return true;
        })));
        registerButton(new DummyButton<>("target", Material.ARROW));
        registerButton(new DummyButton<>("extensions", Material.COMMAND_BLOCK));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "variant_container_" + i);
        }
        update.setButton(47, "target");
        update.setButton(49, "extensions");
        update.setButton(51, "tags");


    }
}