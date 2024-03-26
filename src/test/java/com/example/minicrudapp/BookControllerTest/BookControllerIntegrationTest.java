package com.example.minicrudapp.BookControllerTest;

import com.example.minicrudapp.model.Book;
import com.example.minicrudapp.repo.BookRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.any;
import static org.mockito.BDDMockito.given;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @MockBean
    private BookRepo bookRepo;

    @Autowired
    private MockMvc mockMvc;

    private Book testBook;

    @BeforeEach
    public void setUp() {
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
    }

    @Test
    public void getAllBooks_ReturnsBookList() throws Exception {
        mockMvc.perform(get("/getAllBooks"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == HttpStatus.OK.value()) {
                        assertTrue(result.getResponse().getContentAsString().contains("title"));
                    } else if (status == HttpStatus.NO_CONTENT.value()) {
                        assertEquals("", result.getResponse().getContentAsString());
                    }
                });
    }

    @Test
    public void getBookById_WhenBookExists_ShouldReturnBook() throws Exception {
        Long bookId = 1L;
        Book expectedBook = new Book(1l, "Test Title", "Test Author", "Test Genre", "Test Language");
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(expectedBook));

        mockMvc.perform(get("/getBookById/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value(expectedBook.getTitle()))
                .andExpect(jsonPath("$.author").value(expectedBook.getAuthor()));
    }

    @Test
    public void getBookById_WhenBookDoesNotExist_ShouldReturnNotFound() throws Exception {
        Long bookId = 99L;
        mockMvc.perform(get("/getBookById/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addBookTest() throws Exception {
         }



    @Test
    public void updateBookByIdTest() throws Exception {

    }

    @Test
    public void updateBookById_WhenBookDoesNotExist_ShouldReturnNotFound() throws Exception {
        Long bookId = 99L;
        Book updatedBook = new Book(bookId, "Nonexistent", "Nonexistent", "Nonexistent", "Nonexistent");
        mockMvc.perform(post("/updateBookById/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound());
    }



    @Test
    public void deleteBookById_WhenBookExists_ShouldDeleteAndReturnOk() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(delete("/deleteBookById/{id}", bookId))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteBookById_WhenBookDoesNotExist_ShouldReturnOk() throws Exception {
        Long bookId = 99L;
        mockMvc.perform(delete("/deleteBookById/{id}", bookId))
                .andExpect(status().isOk());
    }

}
