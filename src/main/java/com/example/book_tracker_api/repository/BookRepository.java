package com.example.book_tracker_api.repository;

import com.example.book_tracker_api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
}
