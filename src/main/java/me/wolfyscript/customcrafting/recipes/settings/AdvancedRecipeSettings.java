package me.wolfyscript.customcrafting.recipes.settings;

public class AdvancedRecipeSettings implements CraftingRecipeSettings<AdvancedRecipeSettings> {

    private boolean allowVanillaRecipe;

    public AdvancedRecipeSettings() {
        this.allowVanillaRecipe = true;
    }

    public AdvancedRecipeSettings(AdvancedRecipeSettings settings) {
        this.allowVanillaRecipe = settings.allowVanillaRecipe;
    }

    public boolean isAllowVanillaRecipe() {
        return allowVanillaRecipe;
    }

    public void setAllowVanillaRecipe(boolean allowVanillaRecipe) {
        this.allowVanillaRecipe = allowVanillaRecipe;
    }

    @Override
    public AdvancedRecipeSettings clone() {
        return new AdvancedRecipeSettings(this);
    }
}