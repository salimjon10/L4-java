package org.example;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
class Book {
    private String name;
    private String author;
    private int publishingYear;
    private String isbn;
    private String publisher;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Visitor {
    private String name;
    private String surname;
    private String phone;
    private boolean subscribed;
    private List<Book> favoriteBooks;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class SmsMessage {
    private String phoneNumber;
    private String message;
}

public class Main {
    private List<Visitor> visitors;

    public Main(String filePath) throws IOException {
        Gson gson = new Gson();
        Type visitorListType = new TypeToken<List<Visitor>>() {}.getType();
        this.visitors = gson.fromJson(new FileReader(filePath), visitorListType);
    }

    public void executeTasks() {
        // Задание 1: Вывести список посетителей и их количество
        System.out.println("Количество посетителей: " + visitors.size());
        visitors.forEach(visitor -> System.out.println(visitor.getName() + " " + visitor.getSurname()));
        System.out.println();

        // Задание 2: Вывести список и количество книг, добавленных в избранное, без повторений
        Set<Book> uniqueBooks = visitors.stream()
                .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                .collect(Collectors.toSet());
        System.out.println("Количество уникальных книг в избранном: " + uniqueBooks.size());
        uniqueBooks.forEach(book -> System.out.println(book.getName()));
        System.out.println();

        // Задание 3: Отсортировать по году издания и вывести список книг
        List<Book> sortedBooks = uniqueBooks.stream()
                .sorted(Comparator.comparingInt(Book::getPublishingYear))
                .collect(Collectors.toList());
        System.out.println("Список книг, отсортированных по году издания:");
        sortedBooks.forEach(book -> System.out.println(book.getName() + " - " + book.getPublishingYear()));
        System.out.println();

        // Задание 4: Проверить, есть ли у кого-то в избранном книга автора "Jane Austen"
        boolean hasJaneAusten = visitors.stream()
                .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                .anyMatch(book -> "Jane Austen".equals(book.getAuthor()));
        System.out.println("Есть ли в избранном книги автора 'Jane Austen': " + hasJaneAusten);
        System.out.println();

        // Задание 5: Вывести максимальное число добавленных в избранное книг
        int maxFavorites = visitors.stream()
                .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимальное количество избранных книг: " + maxFavorites);
        System.out.println();

        // Задание 6: Создать SMS-сообщения
        double averageFavorites = visitors.stream()
                .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                .average()
                .orElse(0);

        List<SmsMessage> messages = visitors.stream()
                .filter(Visitor::isSubscribed)
                .map(visitor -> {
                    int favoriteCount = visitor.getFavoriteBooks().size();
                    String message;
                    if (favoriteCount > averageFavorites) {
                        message = "you are a bookworm";
                    } else if (favoriteCount < averageFavorites) {
                        message = "read more";
                    } else {
                        message = "fine";
                    }
                    return new SmsMessage(visitor.getPhone(), message);
                })
                .collect(Collectors.toList());

        // Вывод SMS-сообщений
        System.out.println("Сообщения для подписанных посетителей:");
        messages.forEach(msg -> System.out.println("SMS to " + msg.getPhoneNumber() + ": " + msg.getMessage()));
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/books.json"; // Укажите путь к вашему файлу books.json
            Main library = new Main(filePath);
            library.executeTasks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
