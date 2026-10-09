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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;


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
}
