package com.lms.persistence;
import java.util.ArrayList;
import java.util.List;
public class Course {
    private String id;
    private String Profid;

    private String title;
    private String description;
    private int duration;  // Duration in days or hours
    private List<Lesson> lessons = new ArrayList<>();  // List of lessons
    private List<String> mediaPaths = new ArrayList<>();  // List of media file paths or URLs

    // Constructor, Getters, Setters
    public Course(String id, String title, String description, int duration,String profid) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.Profid = profid;
    }

    public Course() {

    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
    }

    public void addMedia(String mediaPath) {
        this.mediaPaths.add(mediaPath);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }
    public List<String> getMediaPaths() { return mediaPaths; }
    public void setMediaPaths(List<String> mediaPaths) { this.mediaPaths = mediaPaths; }
    public String getProfid() {return Profid;}
    public void setProfid(String profid) {this.Profid = profid;}
}