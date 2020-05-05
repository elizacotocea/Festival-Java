import java.io.Serializable;

public class Entity<ID> implements Serializable {
    private ID id;

    public Entity(ID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public ID getID() {
        return id;
    }
    public void setId(ID id)
    {
        this.id = id;
    }
}