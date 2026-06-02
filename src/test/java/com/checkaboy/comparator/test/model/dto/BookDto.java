package com.checkaboy.comparator.test.model.dto;

/**
 * @author Taras Shaptala
 */
public class BookDto {

    private Long id;
    private String name;
    private AuthorDto author;

    public BookDto() {
    }

    public BookDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public BookDto(Long id, String name, AuthorDto author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthorDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDto author) {
        this.author = author;
    }

}
