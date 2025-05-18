package com.lms.persistence;

public class Lesson {
    private String  id;
    private String title;
    private String content;

    public Lesson(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;

    }

    public Lesson() {

    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

}
