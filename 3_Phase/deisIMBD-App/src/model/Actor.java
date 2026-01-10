package model;

public class Actor {
    private int id;
    private String name;
    private String gender; // 'M' ou 'F'

    public Actor(int id, String name, String gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
}
