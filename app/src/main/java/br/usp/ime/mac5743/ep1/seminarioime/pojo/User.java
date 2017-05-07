package br.usp.ime.mac5743.ep1.seminarioime.pojo;

/**
 * Created by aderleifilho on 30/04/17.
 */

public abstract class User {

    private String nusp;
    private String name;
    private String pass;

    public String getNusp() {
        return nusp;
    }

    public void setNusp(String nusp) {
        this.nusp = nusp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
