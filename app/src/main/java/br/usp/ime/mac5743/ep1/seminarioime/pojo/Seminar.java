package br.usp.ime.mac5743.ep1.seminarioime.pojo;

/**
 * Created by aderleifilho on 06/05/17.
 */

public class Seminar {

    private String seminarId;
    private String seminarName;

    public Seminar(String pSeminarName) {
        this.seminarName = pSeminarName;
    }

    public Seminar(String pSeminarId, String pSeminarName) {
        this.seminarId = pSeminarId;
        this.seminarName = pSeminarName;
    }

    public Seminar() {

    }

    public String getSeminarId() {
        return seminarId;
    }

    public void setSeminarId(String seminarId) {
        this.seminarId = seminarId;
    }

    public String getSeminarName() {
        return seminarName;
    }

    public void setSeminarName(String seminarName) {
        this.seminarName = seminarName;
    }
}
