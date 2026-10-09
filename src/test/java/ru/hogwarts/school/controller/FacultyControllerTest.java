package ru.hogwarts.school.controller;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Test
    void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Adrian Blackwood");
        faculty.setColor("Blue");

        when(facultyService.create(any(Faculty.class)))
                .thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Adrian Blackwood",
                                    "color": "Blue"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Adrian Blackwood"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    void testGetFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Clara Whitmore");
        faculty.setColor("Green");

        when(facultyService.get(1L))
                .thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clara Whitmore"))
                .andExpect(jsonPath("$.color").value("Green"));
    }

    @Test
    void testGetFacultyByColor() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Edmund Ashford");
        faculty.setColor("Blue");

        when(facultyService.getByNameOrColor(null, "Blue"))
                .thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/color")
                        .param("color", "Blue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Edmund Ashford"))
                .andExpect(jsonPath("$[0].color").value("Blue"));
    }

    @Test
    void testGetFacultyStudents() throws Exception {
        Student student = new Student();
        student.setName("Rowan Fairchild");
        student.setAge(20);

        when(facultyService.getStudents(1L))
                .thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Rowan Fairchild"))
                .andExpect(jsonPath("$[0].age").value(20));
    }

    @Test
    void testSearchFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Edmund Ashford");
        faculty.setColor("Blue");

        when(facultyService.getByNameOrColor("Ashford"))
                .thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search")
                        .param("name", "Ashford"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Edmund Ashford"))
                .andExpect(jsonPath("$[0].color").value("Blue"));
    }

    @Test
    void testUpdateFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Edmund Ashford");
        faculty.setColor("Green");

        when(facultyService.update(eq(1L), any(Faculty.class)))
                .thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Edmund Ashford",
                                    "color": "Green"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Edmund Ashford"))
                .andExpect(jsonPath("$.color").value("Green"));
    }

    @Test
    void testGetMissingFaculty() throws Exception {
        when(facultyService.get(123456L))
                .thenReturn(null);

        mockMvc.perform(get("/faculty/123456"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFaculty() throws Exception {
        doNothing().when(facultyService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1"))
                .andExpect(status().isOk());

        verify(facultyService).delete(1L);
    }
}
