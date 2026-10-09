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
        Faculty testResult = restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);

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

        Faculty testFaculty = restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);

        Faculty testResult = restTemplate.getForObject("http://localhost:" + port + "/faculty/"
                + testFaculty.getId(), Faculty.class);

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

        restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);

        Faculty[] testResult = restTemplate.getForObject("http://localhost:" + port
                + "/faculty/color?color=blue", Faculty[].class);

        assertNotNull(testResult);
        assertTrue(java.util.Arrays.stream(testResult)
                .anyMatch(f -> "Smurfingdor".equals(f.getName())
                        && "blue".equals(f.getColor())));

    }

    @Test
    void testGetFacultyStudents() {
        Faculty faculty = new Faculty();
        faculty.setName("Smurfingdor");
        faculty.setColor("blue");

        Faculty testFaculty = restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);

        Student student = new Student();
        student.setName("Smurfetta");
        student.setAge(22);
        student.setFaculty(testFaculty);

        Student testStudent = restTemplate.postForObject("http://localhost:" + port + "/student",
                student, Student.class);

        Student[] testResult = restTemplate.getForObject("http://localhost:" + port + "/faculty/"
                + testFaculty.getId() + "/students", Student[].class);

        assertNotNull(testResult);
        assertTrue(java.util.Arrays.stream(testResult)
                .anyMatch(s -> testStudent.getId().equals(s.getId())
                        && "Smurfetta".equals(s.getName())));
    }

    @Test
    void testGetFacultiesByName() {
        Faculty faculty = new Faculty();
        faculty.setName("Browndor");
        faculty.setColor("brown");

        restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);

        Faculty[] testResult = restTemplate.getForObject("http://localhost:" + port
                        + "/faculty/search?name=Brown", Faculty[].class);

        assertNotNull(testResult);
        assertTrue(java.util.Arrays.stream(testResult)
                .anyMatch(f -> "Browndor".equals(f.getName())));
    }

    @Test
    void testUpdateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Hufflepuff");
        faculty.setColor("yellow");

        Faculty testNewFaculty = restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);

        testNewFaculty.setName("Guffylepuff");
        testNewFaculty.setColor("green");

        restTemplate.put("http://localhost:" + port + "/faculty/" + testNewFaculty.getId(),
                testNewFaculty);

        Faculty testResult = restTemplate.getForObject("http://localhost:" + port
                        + "/faculty/" + testNewFaculty.getId(), Faculty.class);

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

        Faculty createdFaculty = restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);

        restTemplate.delete("http://localhost:" + port + "/faculty/" + createdFaculty.getId());

        Faculty testResult = restTemplate.getForObject("http://localhost:" + port + "/faculty/"
                        + createdFaculty.getId(), Faculty.class);

        assertNull(testResult);
    }
}
