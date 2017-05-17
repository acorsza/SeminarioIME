package br.usp.ime.mac5743.ep1.seminarioime;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestAPIUtilTest {

    private static RestAPIUtil restAPIUtil;

    @BeforeClass
    public static void beforeClass() {
        restAPIUtil = new RestAPIUtil(true);
    }

    // Test response for Login API

    @Test
    public void test01_LoginIfCredentialsAreCorrect_ShouldReturnTrue() {

        String nusp = "TEST";
        String password = "password";
        String role = "Student";

        assertTrue(restAPIUtil.login(nusp, password, role));
    }

    @Test
    public void test02_testLoginIfCredentialsAreIncorrect_ShouldReturnFalse() {

        String nusp = "INCORRECT";
        String password = "wrong";
        String role = "Student";

        assertFalse(restAPIUtil.login(nusp, password, role));
    }

    @Test
    public void test03_LoginIfCredentialsAreNull_ShouldReturnFalse() {

        String nusp = null;
        String password = null;
        String role = null;

        assertFalse(restAPIUtil.login(nusp, password, role));
    }

    // Test response for Student API

    @Test
    public void test04_GetAllStudents_ShouldReturnTrue() {
        assertTrue(restAPIUtil.getAllStudents() instanceof JSONObject);
    }

    @Test
    public void test05_GetStudentIfStudentExists_ShouldReturnTrue() {
        String nusp = "TEST";
        assertTrue(restAPIUtil.getStudent(nusp) instanceof JSONObject);
    }

    @Test
    public void test06_GetStudentIfStudentDoesNotExist_ShouldReturnFalse() {
        String nusp = "INVALID";
        assertFalse(restAPIUtil.getStudent(nusp) instanceof JSONObject);
    }

    @Test
    public void test07_GetStudentIfStudentIDIsNull_ShouldReturnFalse() {
        String nusp = null;
        assertFalse(restAPIUtil.getStudent(nusp) instanceof JSONObject);
    }

    @Test
    public void test08_AddStudentIfStudentIsValid_ShouldReturnNull() {
        String nusp = "TEST";
        String name = "Name";
        String password = "123456";
        assertNull(restAPIUtil.addStudent(nusp, name, password));
    }

    @Test
    public void test09_AddStudentIfStudentIsInvalid_ShouldReturnErrorMessage() {
        String nusp = "INVALID";
        String name = "INVALID";
        String password = "INVALID";
        assertNotNull(restAPIUtil.addStudent(nusp, name, password));
    }

    @Test
    public void test10_AddStudentIfStudentIsNull_ShouldReturnErrorMessage() {
        String nusp = null;
        String name = null;
        String password = null;
        assertNotNull(restAPIUtil.addStudent(nusp, name, password));
    }

    @Test
    public void test11_EditStudentIfStudentIsValid_ShouldReturnNull() {
        String nusp = "TEST";
        String name = "Name";
        String password = "123456";
        assertNull(restAPIUtil.editStudent(nusp, name, password));
    }

    @Test
    public void test12_EditStudentIfStudentIsInvalid_ShouldReturnErrorMessage() {
        String nusp = "INVALID";
        String name = "INVALID";
        String password = "INVALID";
        assertNotNull(restAPIUtil.editStudent(nusp, name, password));
    }

    @Test
    public void test13_EditStudentIfStudentIsNull_ShouldReturnErrorMessage() {
        String nusp = null;
        String name = null;
        String password = null;
        assertNotNull(restAPIUtil.editStudent(nusp, name, password));
    }

    @Test
    public void test14_DeleteStudentIfStudentIsValid_ShouldReturnNull() {
        String nusp = "TEST";
        assertNull(restAPIUtil.deleteStudent(nusp));
    }

    @Test
    public void test15_DeleteStudentIfStudentIsInvalid_ShouldReturnErrorMessage() {
        String nusp = "INVALID";
        assertNotNull(restAPIUtil.deleteStudent(nusp));
    }

    @Test
    public void test16_DeleteStudentIfStudentIsNull_ShouldReturnErrorMessage() {
        String nusp = null;
        assertNotNull(restAPIUtil.deleteStudent(nusp));
    }

    // Test response for Professor API

    @Test
    public void test17_GetAllProfessors_ShouldReturnTrue() {
        assertTrue(restAPIUtil.getAllProfessors() instanceof JSONObject);
    }

    @Test
    public void test18_GetProfessorIfProfessorExists_ShouldReturnTrue() {
        String nusp = "TEST";
        assertTrue(restAPIUtil.getProfessor(nusp) instanceof JSONObject);
    }

    @Test
    public void test19_GetProfessorIfProfessorDoesNotExist_ShouldReturnFalse() {
        String nusp = "INVALID";
        assertFalse(restAPIUtil.getProfessor(nusp) instanceof JSONObject);
    }

    @Test
    public void test20_GetProfessorIfProfessorIDIsNull_ShouldReturnFalse() {
        String nusp = null;
        assertFalse(restAPIUtil.getProfessor(nusp) instanceof JSONObject);
    }

    @Test
    public void test21_AddProfessorIfProfessorIsValid_ShouldReturnNull() {
        String nusp = "TEST";
        String name = "Name";
        String password = "123456";
        assertNull(restAPIUtil.addProfessor(nusp, name, password));
    }

    @Test
    public void test22_AddProfessorIfProfessorIsInvalid_ShouldReturnErrorMessage() {
        String nusp = "INVALID";
        String name = "INVALID";
        String password = "INVALID";
        assertNotNull(restAPIUtil.addProfessor(nusp, name, password));
    }

    @Test
    public void test23_AddProfessorIfProfessorIsNull_ShouldReturnErrorMessage() {
        String nusp = null;
        String name = null;
        String password = null;
        assertNotNull(restAPIUtil.addProfessor(nusp, name, password));
    }

    @Test
    public void test24_EditProfessorIfProfessorIsValid_ShouldReturnNull() {
        String nusp = "TEST";
        String name = "Name";
        String password = "123456";
        assertNull(restAPIUtil.editProfessor(nusp, name, password));
    }

    @Test
    public void test25_EditProfessorIfProfessorIsInvalid_ShouldReturnErrorMessage() {
        String nusp = "INVALID";
        String name = "INVALID";
        String password = "INVALID";
        assertNotNull(restAPIUtil.editProfessor(nusp, name, password));
    }

    @Test
    public void test26_EditProfessorIfProfessorIsNull_ShouldReturnErrorMessage() {
        String nusp = null;
        String name = null;
        String password = null;
        assertNotNull(restAPIUtil.editProfessor(nusp, name, password));
    }

    @Test
    public void test27_DeleteProfessorIfProfessorIsValid_ShouldReturnNull() {
        String nusp = "TEST";
        assertNull(restAPIUtil.deleteProfessor(nusp));
    }

    @Test
    public void test28_DeleteProfessorIfProfessorIsInvalid_ShouldReturnErrorMessage() {
        String nusp = "INVALID";
        assertNotNull(restAPIUtil.deleteProfessor(nusp));
    }

    @Test
    public void test29_DeleteProfessorIfProfessorIsNull_ShouldReturnErrorMessage() {
        String nusp = null;
        assertNotNull(restAPIUtil.deleteProfessor(nusp));
    }

    // Test response for Seminar API

    @Test
    public void test30_GetAllSeminars_ShouldReturnTrue() {
        assertTrue(restAPIUtil.getAllSeminars() instanceof JSONObject);
    }

    @Test
    public void test31_GetSeminarIfSeminarExists_ShouldReturnTrue() {
        String seminarId = "TEST";
        assertTrue(restAPIUtil.getSeminar(seminarId) instanceof JSONObject);
    }

    @Test
    public void test32_GetSeminarIfSeminarDoesNotExist_ShouldReturnFalse() {
        String seminarId = "INVALID";
        assertFalse(restAPIUtil.getSeminar(seminarId) instanceof JSONObject);
    }

    @Test
    public void test33_GetSeminarIfSeminarIDIsNull_ShouldReturnFalse() {
        String seminarId = null;
        assertFalse(restAPIUtil.getSeminar(seminarId) instanceof JSONObject);
    }

    @Test
    public void test34_AddSeminarIfSeminarIsValid_ShouldReturnNull() {
        String seminarName = "SEMINAR";
        assertTrue(restAPIUtil.addSeminar(seminarName));
    }

    @Test
    public void test35_AddSeminarIfSeminarIsInvalid_ShouldReturnErrorMessage() {
        String seminarName = "INVALID";
        assertFalse(restAPIUtil.addSeminar(seminarName));
    }

    @Test
    public void test36_AddSeminarIfSeminarIsNull_ShouldReturnErrorMessage() {
        String seminarName = null;
        assertFalse(restAPIUtil.addSeminar(seminarName));
    }

    @Test
    public void test37_EditSeminarIfSeminarIsValid_ShouldReturnNull() {
        String seminarId = "SEMINAR";
        String seminarName = "Name";
        assertNull(restAPIUtil.editSeminar(seminarId, seminarName));
    }

    @Test
    public void test38_EditSeminarIfSeminarIsInvalid_ShouldReturnErrorMessage() {
        String seminarId = "INVALID";
        String seminarName = "Name";
        assertNotNull(restAPIUtil.editSeminar(seminarId, seminarName));
    }

    @Test
    public void test39_EditSeminarIfSeminarIsNull_ShouldReturnErrorMessage() {
        String seminarId = null;
        String seminarName = null;
        assertNotNull(restAPIUtil.editSeminar(seminarId, seminarName));
    }

    @Test
    public void test40_DeleteSeminarIfSeminarIsValid_ShouldReturnNull() {
        String seminarId = "SEMINAR";
        assertNull(restAPIUtil.deleteSeminar(seminarId));
    }

    @Test
    public void test41_DeleteSeminarIfSeminarIsInvalid_ShouldReturnErrorMessage() {
        String seminarId = "INVALID";
        assertNotNull(restAPIUtil.deleteSeminar(seminarId));
    }

    @Test
    public void test42_DeleteSeminarIfSeminarIsNull_ShouldReturnErrorMessage() {
        String seminarId = null;
        assertNotNull(restAPIUtil.deleteSeminar(seminarId));
    }
}
