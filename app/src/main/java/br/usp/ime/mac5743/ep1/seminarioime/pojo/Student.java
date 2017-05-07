package br.usp.ime.mac5743.ep1.seminarioime.pojo;

/**
 * Created by aderleifilho on 30/04/17.
 */

public class Student extends User {
    public Student(String pNusp, String pName, String pPassword) {
        super();
        super.setNusp(pNusp);
        super.setName(pName);
        super.setPass(pPassword);
    }

    public Student() {
        super();
    }
}
