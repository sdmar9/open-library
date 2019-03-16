package com.example.open_library;

import com.google.android.gms.maps.model.LatLng;

public class Book {

    private String isbn;
    private String title;
    private String author;
    private String url;
    private String description;
    private LatLng location;
    private String state;
    private String requestState;
    private String bookId;

    public Book(String _isbn, String _title, String _author, String _url, String _description, LatLng _location) {
        this.isbn = _isbn;
        this.author = _author;
        this.title = _title;
        this.url = _url;
        this.description = _description;
        this.location = _location;
        this.state = "None";
        this.requestState = "None";
        this.bookId = "";
    }
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setRequestState(String requestState) {
        this.requestState = requestState;
    }

    public String getRequestState() {
        return requestState;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }
}
