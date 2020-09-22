package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.elite_crafting.buttons.CraftingSlotButton;
import me.wolfyscript.customcrafting.gui.elite_crafting.buttons.ResultSlotButton;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CraftingWindow5 extends ExtendedGuiWindow {

    public CraftingWindow5(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("crafting_grid5", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 25; i++) {
            registerButton(new CraftingSlotButton(i, customCrafting));
        }
        registerButton(new ResultSlotButton(customCrafting));
        registerButton(new DummyButton("texture_light", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9005)));
        registerButton(new DummyButton("texture_dark", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9015)));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
        EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
        if (eliteWorkbench.getContents() == null || eliteWorkbench.getCurrentGridSize() <= 0) {
            eliteWorkbench.setCurrentGridSize(5);
            eliteWorkbench.setContents(new ItemStack[25]);
        }

        event.setButton(18, "crafting", "knowledge_book");
        int slot;
        for (int i = 0; i < 25; i++) {
            slot = 1 + i + (i / 5) * 4;
            event.setButton(slot, "crafting.slot_" + i);
        }
        event.setButton(25, "result_slot");
    }


}