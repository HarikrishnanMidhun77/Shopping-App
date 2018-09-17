package com.example.mithun.myapplication;



public class Movie {
    private String title, genre, year,id;

    public Movie() {
    }

    public Movie(String id,String title, String genre, String year) {
        this.id=id;
        this.title = title;
        this.genre = genre;
        this.year = year;
    }
    public String getId() {
        return id;
    }

    public void setId(String name) {
        this.id = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}