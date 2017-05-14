package br.usp.ime.mac5743.ep1.seminarioime.util;

/**
 * Created by aderleifilho on 30/04/17.
 */

public enum Roles {
    PROFESSOR ("PROFESSOR"),
    STUDENT ("STUDENT");

    private final String name;

    Roles(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
