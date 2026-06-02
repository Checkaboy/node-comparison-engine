package com.checkaboy.comparator.test.model.entity;

/**
 * @author Taras Shaptala
 */
public class BookEntity {

    private Long id;
    private String name;
    private AuthorEntity author;

    public BookEntity() {
    }

    public BookEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public BookEntity(Long id, String name, AuthorEntity author) {
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

    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

}
