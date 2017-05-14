package br.usp.ime.mac5743.ep1.seminarioime.util;

/**
 * Created by aderleifilho on 06/05/17.
 */

public enum Preferences {
    NUSP ("NUSP"),
    NAME ("NAME"),
    FULL_NAME ("FULL_NAME"),
    PASSWORD ("PASSWORD"),
    ROLE ("ROLE"),
    AUTO_CONNECT ("AUTO_CONNECT");

    private final String name;

    Preferences(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
