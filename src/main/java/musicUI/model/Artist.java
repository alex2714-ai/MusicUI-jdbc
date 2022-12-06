package musicUI.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Artist {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;

    public Artist(int id,String name) {
        this.id=new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
    }

    public Artist() {
        this.id=new SimpleIntegerProperty();
        this.name=new SimpleStringProperty();
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }


}
