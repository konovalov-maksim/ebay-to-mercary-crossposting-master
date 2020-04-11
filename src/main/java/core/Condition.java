package core;

import java.util.*;

public class Condition {

    private final int id;

    public Condition(int id) {
        if (!conditions.containsKey(id)) throw new IllegalArgumentException("Unknown condition id: " + id);
        this.id = id;
    }

    public String getName() {
        return conditions.get(id);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static List<Condition> getAllConditions() {
        List<Condition> allConditions = new ArrayList<>();
        for (Integer id : conditions.keySet()) allConditions.add(new Condition(id));
        return allConditions;
    }

    private static Map<Integer, String> conditions = new HashMap<>();
    static {
        conditions.put(1, "New");
        conditions.put(2, "Like New");
        conditions.put(3, "Good");
        conditions.put(4, "Fair");
        conditions.put(5, "Poor");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return id == condition.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
