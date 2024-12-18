package com.bookstore.util;

import com.bookstore.dao.BookDao;
import com.bookstore.dao.OperationLogDAO;
import com.bookstore.dao.UserDao;
import com.bookstore.model.Book;
import com.bookstore.model.OperationLog;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataImportExport {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Connection conn;

    public DataImportExport(Connection conn) {
        this.conn = conn;
    }

    // Book import/export
    public void exportBooksToCSV(String filePath) throws IOException {
        BookDao bookDao = new BookDao(conn);
        List<Book> books = bookDao.getAllBooks();

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(new String[]{"ISBN", "Title", "Author", "Publisher", "Price", "Stock"});

            // Write data
            for (Book book : books) {
                writer.writeNext(new String[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    String.valueOf(book.getPrice()),
                    String.valueOf(book.getStock())
                });
            }
        }
    }

    public void importBooksFromCSV(String filePath) throws IOException, CsvException {
        BookDao bookDao = new BookDao(conn);

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Skip header
            reader.skip(1);
            
            String[] line;
            while ((line = reader.readNext()) != null) {
                Book book = new Book();
                book.setIsbn(line[0]);
                book.setTitle(line[1]);
                book.setAuthor(line[2]);
                book.setPublisher(line[3]);
                book.setPrice(Double.parseDouble(line[4]));
                book.setStock(Integer.parseInt(line[5]));
                
                bookDao.addBook(book);
            }
        }
    }

    // Order export
    public void exportOrdersToCSV(String filePath) throws IOException {
        OrderDao orderDao = new OrderDao(conn);
        List<Order> orders = orderDao.getAllOrders();

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(new String[]{"Order ID", "User", "Order Date", "Total Amount", "Status"});

            // Write data
            for (Order order : orders) {
                writer.writeNext(new String[]{
                    String.valueOf(order.getId()),
                    order.getUsername(),
                    order.getOrderDate().format(DATE_FORMATTER),
                    String.valueOf(order.getTotalAmount()),
                    order.getStatus().toString()
                });
            }
        }
    }

    // User export (excluding sensitive information)
    public void exportUsersToCSV(String filePath) throws IOException {
        UserDao userDao = new UserDao(conn);
        List<User> users = userDao.getAllUsers();

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(new String[]{"Username", "Email", "Phone", "Role", "Active"});

            // Write data
            for (User user : users) {
                writer.writeNext(new String[]{
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole().toString(),
                    String.valueOf(user.isActive())
                });
            }
        }
    }

    // Operation logs export
    public void exportLogsToCSV(String filePath) throws IOException {
        OperationLogDAO logDao = new OperationLogDAO(conn);
        List<OperationLog> logs = logDao.getLogs(1000, 0); // Get latest 1000 logs

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(new String[]{"Timestamp", "Username", "Operation", "Details"});

            // Write data
            for (OperationLog log : logs) {
                writer.writeNext(new String[]{
                    log.getTimestamp().format(DATE_FORMATTER),
                    log.getUsername(),
                    log.getOperation(),
                    log.getDetails()
                });
            }
        }
    }
} 