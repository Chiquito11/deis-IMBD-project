package model;

public class Director {
    private int id;
    private String name;
    private boolean hidden;

    public Director(int id, String name, boolean hidden) {
        this.id = id;
        this.name = name;
        this.hidden = hidden;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isHidden() { return hidden; }
}
