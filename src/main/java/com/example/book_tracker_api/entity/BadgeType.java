package com.example.book_tracker_api.entity;

public enum BadgeType {
    FIRST_BOOK("First Book", "Finished your first book!"),
    BOOKWORM("Bookworm", "Finished 10 books"),
    BIBLIOPHILE("Bibliophile", "Finished 50 books"),
    SCHOLAR("Scholar", "Finished 100 books"),
    FIRST_REVIEW("First Review", "Wrote your first review"),
    CRITIC("Critic", "Wrote 10 reviews");

    private final String title;
    private final String description;

    BadgeType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
