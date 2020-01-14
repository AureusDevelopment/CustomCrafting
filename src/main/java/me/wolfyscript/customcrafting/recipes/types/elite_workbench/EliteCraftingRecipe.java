package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EliteCraftingRecipe implements CraftingRecipe<EliteCraftConfig> {

    private boolean exactMeta, hidden;
    private RecipePriority priority;
    private Conditions conditions;

    private EliteCraftConfig config;
    private String id;
    private String group;
    private List<CustomItem> result;
    private Map<Character, List<CustomItem>> ingredients;
    private WolfyUtilities api;
    protected int requiredGridSize;

    public EliteCraftingRecipe(EliteCraftConfig config) {
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
        this.group = config.getGroup();
        this.priority = config.getPriority();
        this.api = CustomCrafting.getApi();
        this.exactMeta = config.isExactMeta();
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();

        this.requiredGridSize = 6;
        if (config.isShapeless()){
            if(ingredients.size() <= 9){
                requiredGridSize = 3;
            }else if (ingredients.size() <= 16){
                requiredGridSize = 4;
            }else if (ingredients.size() <= 25){
                requiredGridSize = 5;
            }else if (ingredients.size() <= 36){
                requiredGridSize = 6;
            }
        }else{
            config.getShape();
        }
    }

    @Override
    public void load() {
    }

    @Override
    public void setIngredients(Map<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public Map<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    @Override
    public List<CustomItem> getIngredients(int slot) {
        return getIngredients().getOrDefault(LETTERS[slot], new ArrayList<>());
    }

    @Override
    public CustomItem getIngredient(int slot) {
        List<CustomItem> list = getIngredients(slot);
        return list.size() > 0 ? list.get(0) : null;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    @Override
    public CustomItem getCustomResult() {
        return getCustomResults().get(0);
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    public EliteCraftConfig getConfig() {
        return config;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }
}
