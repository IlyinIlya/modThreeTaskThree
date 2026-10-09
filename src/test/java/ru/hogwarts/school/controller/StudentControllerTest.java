package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        ResponseEntity<Student> response = restTemplate.postForEntity("http://localhost:" + port + "/student",
                student, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Student testResult = response.getBody();
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

        ResponseEntity<Student> createResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/student", student, Student.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Student testStudent = createResponse.getBody();
        assertNotNull(testStudent);
        assertNotNull(testStudent.getId());

        ResponseEntity<Student> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/student/" + testStudent.getId(), Student.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Student testResult = getResponse.getBody();
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

        ResponseEntity<Student> createResponse1 = restTemplate.postForEntity("http://localhost:" + port
                + "/student", testStudent1, Student.class);
        assertEquals(HttpStatus.OK, createResponse1.getStatusCode());
        assertNotNull(createResponse1.getBody());

        ResponseEntity<Student> createResponse2 = restTemplate.postForEntity("http://localhost:" + port
                + "/student", testStudent2, Student.class);
        assertEquals(HttpStatus.OK, createResponse2.getStatusCode());
        assertNotNull(createResponse2.getBody());

        ResponseEntity<Student[]> response = restTemplate.getForEntity("http://localhost:" + port
                + "/student/age?min=17&max=18", Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Student[] testResult = response.getBody();
        assertNotNull(testResult);
        assertTrue(java.util.Arrays
                .stream(testResult).anyMatch(student -> "Max Sedrik"
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

        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", testFaculty, Faculty.class);
        assertEquals(HttpStatus.OK, facultyResponse.getStatusCode());

        Faculty testNewFaculty = facultyResponse.getBody();
        assertNotNull(testNewFaculty);
        assertNotNull(testNewFaculty.getId());

        Student testStudent = new Student();
        testStudent.setName("Marty Makfly");
        testStudent.setAge(23);
        testStudent.setFaculty(testNewFaculty);

        ResponseEntity<Student> studentResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/student", testStudent, Student.class);
        assertEquals(HttpStatus.OK, studentResponse.getStatusCode());

        Student testNewStudent = studentResponse.getBody();
        assertNotNull(testNewStudent);
        assertNotNull(testNewStudent.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity("http://localhost:" + port
                + "/student/" + testNewStudent.getId() + "/faculty", Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Faculty testResult = response.getBody();
        assertNotNull(testResult);
        assertEquals(testNewFaculty.getId(), testResult.getId());
        assertEquals("Gryffindor", testResult.getName());
        assertEquals("orange", testResult.getColor());
    }

    @Test
    void testUpdateStudent() {
        Student student = new Student();
        student.setName("Peter Parker");
        student.setAge(21);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/student", student, Student.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Student testNewStudent = createResponse.getBody();
        assertNotNull(testNewStudent);
        assertNotNull(testNewStudent.getId());

        testNewStudent.setName("Mary Jonsons");
        testNewStudent.setAge(22);

        ResponseEntity<Void> updateResponse = restTemplate.exchange("http://localhost:" + port
                        + "/student/" + testNewStudent.getId(), org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(testNewStudent), Void.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        ResponseEntity<Student> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/student/" + testNewStudent.getId(), Student.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Student testResult = getResponse.getBody();
        assertNotNull(testResult);
        assertEquals(testNewStudent.getId(), testResult.getId());
        assertEquals("Mary Jonsons", testResult.getName());
        assertEquals(22, testResult.getAge());
    }

    @Test
    void testDeleteStudent() {
        Student student = new Student();
        student.setName("Peter Parker");
        student.setAge(21);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity("http://localhost:" + port + "/student",
                student, Student.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Student testNewStudent = createResponse.getBody();
        assertNotNull(testNewStudent);
        assertNotNull(testNewStudent.getId());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange("http://localhost:" + port
                        + "/student/" + testNewStudent.getId(), org.springframework.http.HttpMethod.DELETE,
                null, Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/student/" + testNewStudent.getId(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void testGetMissingStudent() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port
                + "/student/123456", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}