package obj.quickblox.sample.chat.java.ui.Model;

public class CardsModel {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCards_images() {
        return cards_images;
    }

    public void setCards_images(String cards_images) {
        this.cards_images = cards_images;
    }

    String id ;
    String cards_images;

    public String getCategories() {
        return Categories;
    }

    public void setCategories(String categories) {
        Categories = categories;
    }

    String Categories;
}
