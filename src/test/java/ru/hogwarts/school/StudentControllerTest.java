package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class StudentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void testCreateStudent() {
        Student student = new Student();
        student.setName("Peter Parker");
        student.setAge(21);

        Student testResult = restTemplate.postForObject("http://localhost:" + port + "/student",
                student, Student.class);
        assertNotNull(testResult);
        assertNotNull(testResult.getId());
        assertEquals("Peter Parker", testResult.getName());
        assertEquals(21, testResult.getAge());
    }

    @Test
    void testGetStudent() {
        Student student = new Student();
        student.setName("Missy Pickles");
        student.setAge(19);

        Student testStudent = restTemplate.postForObject("http://localhost:" + port + "/student",
                student, Student.class);
        Student testResult = restTemplate.getForObject("http://localhost:" + port + "/student/" +
                testStudent.getId(), Student.class);

        assertNotNull(testResult);
        assertEquals(testStudent.getId(), testResult.getId());
        assertEquals("Missy Pickles", testResult.getName());
        assertEquals(19, testResult.getAge());
    }

    @Test
    void testGetStudentsByAge() {
        Student testStudent1 = new Student();
        testStudent1.setName("Max Sedrik");
        testStudent1.setAge(17);

        Student testStudent2 = new Student();
        testStudent2.setName("Yota Rezidi");
        testStudent2.setAge(20);

        restTemplate.postForObject("http://localhost:" + port + "/student",
                testStudent1, Student.class);
        restTemplate.postForObject("http://localhost:" + port + "/student",
                testStudent2, Student.class);

        Student[] testResult = restTemplate.getForObject(
                "http://localhost:" + port + "/student/age?min=17&max=18",
                Student[].class);

        assertNotNull(testResult);
        assertTrue(java.util.Arrays
                .stream(testResult)
                .anyMatch(student -> "Max Sedrik"
                        .equals(student.getName()) && student.getAge() == 17));
        for (Student student : testResult) {
            assertTrue(student.getAge() >= 17);
            assertTrue(student.getAge() <= 18);
        }
    }

    @Test
    void testGetStudentFaculty() {
        Faculty testFaculty = new Faculty();
        testFaculty.setName("Gryffindor");
        testFaculty.setColor("orange");
        Faculty testNewFaculty = restTemplate.postForObject("http://localhost:" + port + "/faculty",
                testFaculty, Faculty.class);

        Student testStudent = new Student();
        testStudent.setName("Marty Makfly");
        testStudent.setAge(23);
        testStudent.setFaculty(testNewFaculty);
        Student testNewStudent = restTemplate.postForObject("http://localhost:" + port + "/student",
                testStudent, Student.class);

        Faculty testResult = restTemplate.getForObject("http://localhost:" + port + "/student/"
                + testNewStudent.getId() + "/faculty", Faculty.class);

        assertNotNull(testResult);
        assertEquals(testNewFaculty.getId(), testResult.getId());
        assertEquals("Gryffindor", testResult.getName());
        assertEquals("orange", testResult.getColor());
    }
}
