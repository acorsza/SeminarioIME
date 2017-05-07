package br.usp.ime.mac5743.ep1.seminarioime.api;

/**
 * Created by aderleifilho on 28/04/17.
 */

public interface RestRoutes {
    String BASE_URL             = "http://207.38.82.139:8001/";

    String LOGIN_STUDENT        = BASE_URL + "login/student";
    String LOGIN_PROFESSOR      = BASE_URL + "login/teacher";

    String GET_ALL_STUDENTS     = BASE_URL + "student";
    String GET_STUDENT          = BASE_URL + "student/get/%s";
    String ADD_STUDENT          = BASE_URL + "student/add";
    String EDIT_STUDENT         = BASE_URL + "student/edit";
    String DELETE_STUDENT       = BASE_URL + "student/delete";

    String GET_ALL_PROFESSORS   = BASE_URL + "teacher";
    String GET_PROFESSOR        = BASE_URL + "teacher/get/%s";
    String ADD_PROFESSOR        = BASE_URL + "teacher/add";
    String EDIT_PROFESSOR       = BASE_URL + "teacher/edit";
    String DELETE_PROFESSOR     = BASE_URL + "teacher/delete";

    String GET_ALL_SEMINARS     = BASE_URL + "seminar";
    String GET_SEMINAR          = BASE_URL + "seminar/get/%s";
    String ADD_SEMINAR          = BASE_URL + "seminar/add";
    String EDIT_SEMINAR         = BASE_URL + "seminar/edit";
    String DELETE_SEMINAR       = BASE_URL + "seminar/delete";

    String SUBMIT_ATTENDANCE    = BASE_URL + "attendence/submit";
}
