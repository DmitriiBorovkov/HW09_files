package models;

public class Meals {
    private String title;
    private Integer number;
    Ingredients ingredients;

    public Ingredients getIngredients() {
        return ingredients;
    }

    public String getTitle() {
        return title;
    }

    public Integer getNumber() {
        return number;
    }
}
