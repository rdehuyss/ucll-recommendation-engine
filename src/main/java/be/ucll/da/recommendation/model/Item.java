package be.ucll.da.recommendation.model;

import java.util.UUID;

public class Item {
    String content;
    public Item(String s) {
        content = s;
    }

    public Item(UUID ratedItemId) {
        this.content = ratedItemId.toString();
    }

    public int hashCode() { return content.hashCode();}
    public String toString() { return content; }

    @Override
    public boolean equals(Object obj) {
        return this.content.equals(((Item)obj).content);
    }
}
