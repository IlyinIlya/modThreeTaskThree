package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class FacultyControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("blue");

        ResponseEntity<Faculty> response = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Faculty testResult = response.getBody();
        assertNotNull(testResult);
        assertNotNull(testResult.getId());
        assertEquals("Ravenclaw", testResult.getName());
        assertEquals("blue", testResult.getColor());
    }

    @Test
    void testGetFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Hufflepuff");
        faculty.setColor("yellow");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Faculty testFaculty = createResponse.getBody();
        assertNotNull(testFaculty);
        assertNotNull(testFaculty.getId());

        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/faculty/" + testFaculty.getId(), Faculty.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Faculty testResult = getResponse.getBody();
        assertNotNull(testResult);
        assertEquals(testFaculty.getId(), testResult.getId());
        assertEquals("Hufflepuff", testResult.getName());
        assertEquals("yellow", testResult.getColor());
    }

    @Test
    void testGetFacultiesByColor() {
        Faculty faculty = new Faculty();
        faculty.setName("Smurfingdor");
        faculty.setColor("blue");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Faculty createdFaculty = createResponse.getBody();
        assertNotNull(createdFaculty);

        ResponseEntity<Faculty[]> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/faculty/color?color=blue", Faculty[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Faculty[] testResult = getResponse.getBody();
        assertNotNull(testResult);
        assertTrue(java.util.Arrays.stream(testResult)
                .anyMatch(f -> createdFaculty.getId().equals(f.getId())
                        && "Smurfingdor".equals(f.getName())
                        && "blue".equals(f.getColor())));
    }

    @Test
    void testGetFacultyStudents() {
        Faculty faculty = new Faculty();
        faculty.setName("Smurfingdor");
        faculty.setColor("blue");

        ResponseEntity<Faculty> createFacultyResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, createFacultyResponse.getStatusCode());

        Faculty testFaculty = createFacultyResponse.getBody();
        assertNotNull(testFaculty);
        assertNotNull(testFaculty.getId());

        Student student = new Student();
        student.setName("Smurfetta");
        student.setAge(22);
        student.setFaculty(testFaculty);

        ResponseEntity<Student> createStudentResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/student", student, Student.class);
        assertEquals(HttpStatus.OK, createStudentResponse.getStatusCode());

        Student testStudent = createStudentResponse.getBody();
        assertNotNull(testStudent);
        assertNotNull(testStudent.getId());

        ResponseEntity<Student[]> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/faculty/" + testFaculty.getId() + "/students", Student[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Student[] testResult = getResponse.getBody();
        assertNotNull(testResult);
        assertTrue(java.util.Arrays.stream(testResult)
                .anyMatch(s -> testStudent.getId().equals(s.getId())
                        && "Smurfetta".equals(s.getName())
                        && s.getAge() == 22));
    }

    @Test
    void testGetFacultiesByName() {
        Faculty faculty = new Faculty();
        faculty.setName("Browndor");
        faculty.setColor("brown");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Faculty createdFaculty = createResponse.getBody();
        assertNotNull(createdFaculty);

        ResponseEntity<Faculty[]> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/faculty/search?name=Brown", Faculty[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Faculty[] testResult = getResponse.getBody();
        assertNotNull(testResult);
        assertTrue(java.util.Arrays.stream(testResult)
                .anyMatch(f -> createdFaculty.getId().equals(f.getId())
                        && "Browndor".equals(f.getName())
                        && "brown".equals(f.getColor())));
    }

    @Test
    void testUpdateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Hufflepuff");
        faculty.setColor("yellow");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Faculty testNewFaculty = createResponse.getBody();
        assertNotNull(testNewFaculty);
        assertNotNull(testNewFaculty.getId());

        testNewFaculty.setName("Guffylepuff");
        testNewFaculty.setColor("green");

        ResponseEntity<Faculty> updateResponse = restTemplate.exchange("http://localhost:" + port
                        + "/faculty/" + testNewFaculty.getId(), HttpMethod.PUT,
                new HttpEntity<>(testNewFaculty), Faculty.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        Faculty updatedFaculty = updateResponse.getBody();
        assertNotNull(updatedFaculty);
        assertEquals(testNewFaculty.getId(), updatedFaculty.getId());
        assertEquals("Guffylepuff", updatedFaculty.getName());
        assertEquals("green", updatedFaculty.getColor());

        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/faculty/" + testNewFaculty.getId(), Faculty.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Faculty testResult = getResponse.getBody();
        assertNotNull(testResult);
        assertEquals(testNewFaculty.getId(), testResult.getId());
        assertEquals("Guffylepuff", testResult.getName());
        assertEquals("green", testResult.getColor());
    }

    @Test
    void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Boring Faculty");
        faculty.setColor("black");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity("http://localhost:" + port
                + "/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Faculty createdFaculty = createResponse.getBody();
        assertNotNull(createdFaculty);
        assertNotNull(createdFaculty.getId());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange("http://localhost:" + port
                + "/faculty/" + createdFaculty.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity("http://localhost:" + port
                + "/faculty/" + createdFaculty.getId(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void testGetMissingFaculty() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port
                + "/faculty/123456", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
