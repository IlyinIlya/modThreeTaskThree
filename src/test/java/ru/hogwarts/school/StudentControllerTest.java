package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;


@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student();
        student.setName("Alaric Moonwhistle");
        student.setAge(20);

        Mockito.when(studentService.create(Mockito.any(Student.class)))
                .thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Alaric Moonwhistle",
                                    "age": 20
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alaric Moonwhistle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(20));
    }

    @Test
    void testGetStudent() throws Exception {
        Student student = new Student();
        student.setName("Cedric Thornwick");
        student.setAge(21);

        Mockito.when(studentService.get(1L))
                .thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Cedric Thornwick"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(21));
    }

    @Test
    void testGetStudentsByAge() throws Exception {
        Student firstStudent = new Student();
        firstStudent.setName("Elana Nightbloom");
        firstStudent.setAge(20);

        Student secondStudent = new Student();
        secondStudent.setName("Mary Ashgrove");
        secondStudent.setAge(21);

        Mockito.when(studentService.getByAge(20, 21))
                .thenReturn(List.of(firstStudent, secondStudent));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age")
                        .param("min", "20")
                        .param("max", "21"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Elana Nightbloom"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Mary Ashgrove"));
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Aster Frostvale");
        faculty.setColor("Mike Blue");

        Mockito.when(studentService.getFaculty(1L))
                .thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1/faculty"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Aster Frostvale"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("Mike Blue"));
    }

    @Test
    void testUpdateStudent() throws Exception {
        Student student = new Student();
        student.setName("Alaric Moonwhistle");
        student.setAge(22);

        Mockito.when(studentService.update(
                        Mockito.eq(1L),
                        Mockito.any(Student.class)))
                .thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Alaric Moonwhistle",
                                    "age": 22
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alaric Moonwhistle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(22));
    }

    @Test
    void testDeleteStudent() throws Exception {
        Mockito.doNothing()
                .when(studentService)
                .delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(studentService).delete(1L);
    }
}
