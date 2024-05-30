import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static int readerID = 1;
    private static final List<Book> books = new ArrayList<>();
    private static final List<Reader> readers = new ArrayList<>();
    private static final List<BookTransaction> transactions = new ArrayList<>();

    public static void main(String[] args) {
        readBooksFromFile();
        readReadersFromFile();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Выберите действие:");
            System.out.println("1. Просмотреть все книги");
            System.out.println("2. Зарегистрировать читателя");
            System.out.println("3. Выдать книгу");
            System.out.println("4. Вернуть книгу");
            System.out.println("5. Выход");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Список всех книг:");
                    for (Book book : books) {
                        System.out.println(book);
                    }
                    break;
                case 2:
                    registerReader();
                    break;
                case 3:
                    BookTransaction transaction = issueBook(books, readers);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                    break;
                case 4:
                    returnBook(transactions);
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, выберите снова.");
            }
        }
    }

    static class Book {
        private int book_id;
        private String title;
        private String author;
        private String genre;
        private int publication_year;

        public Book(int book_id, String title, String author, String genre, int publication_year) {
            this.book_id = book_id;
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.publication_year = publication_year;
        }

        @Override
        public String toString() {
            return "ID книги: " + book_id + ", Название: " + title + ", Автор: " + author + ", Жанр: " + genre + ", Год публикации: " + publication_year;
        }
    }

    static class Reader {
        private final int reader_id;
        private final String name;
        private final String surname;
        private final String birthDate;
        private final String passportData;
        private final String contactPhone;
        private final String email;

        public Reader(int readerId, String name, String surname, String birthDate, String passportData, String contactPhone, String email) {
            this.reader_id = readerId;
            this.name = name;
            this.surname = surname;
            this.birthDate = birthDate;
            this.passportData = passportData;
            this.contactPhone = contactPhone;
            this.email = email;
        }

        public int getReaderId() {
            return reader_id;
        }
    }

    static class BookTransaction {
        private final int transaction_id;
        int book_id;
        int reader_id;
        String issue_date;
        String due_date;
        String return_date;

        public BookTransaction(int transaction_id, int book_id, int reader_id, String issue_date, String due_date) {
            this.transaction_id = transaction_id;
            this.book_id = book_id;
            this.reader_id = reader_id;
            this.issue_date = issue_date;
            this.due_date = due_date;
            this.return_date = ""; // По умолчанию пустая строка, так как книга еще не возвращена
        }

        public int getTransactionId() {
            return transaction_id;
        }

        public String getReturnDate() {
            return return_date;
        }

        public void setReturnDate(String returnDate) {
            this.return_date = returnDate;
        }
    }

    private static void readBooksFromFile() {
        try {
            File file = new File("books.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                int bookId = Integer.parseInt(parts[0]);
                String title = parts[1];
                String author = parts[2];
                String genre = parts[3];
                int publicationYear = Integer.parseInt(parts[4]);
                Book book = new Book(bookId, title, author, genre, publicationYear);
                books.add(book);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка при чтении файла книг.");
            e.printStackTrace();
        }
    }

    private static void registerReader() {
        try {
            FileWriter writer = new FileWriter("readers.txt", true);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите данные читателя:");
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            System.out.print("Фамилия: ");
            String surname = scanner.nextLine();
            System.out.print("Дата рождения: ");
            String birthDate = scanner.nextLine();
            System.out.print("Паспортные данные: ");
            String passportData = scanner.nextLine();
            System.out.print("Контактный телефон: ");
            String contactPhone = scanner.nextLine();
            System.out.print("Электронная почта: ");
            String email = scanner.nextLine();
            // Записываем данные читателя в файл
            writer.write(readerID + ",");
            writer.write(name + ",");
            writer.write(surname + ",");
            writer.write(birthDate + ",");
            writer.write(passportData + ",");
            writer.write(contactPhone + ",");
            writer.write(email + "\n");
            // Увеличиваем ID для следующего читателя
            readerID++;
            writer.close();
            System.out.println("Данные читателя сохранены в файл readers.txt");
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл.");
            e.printStackTrace();
        }
        System.out.println("Читатель добавлен.");
    }

    private static void readReadersFromFile() {
        try {
            File file = new File("readers.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                int readerId = Integer.parseInt(parts[0]);
                String name = parts[1];
                String surname = parts[2];
                String birthDate = parts[3];
                String passportData = parts[4];
                String contactPhone = parts[5];
                String email = parts[6];
                Reader reader = new Reader(readerId, name, surname, birthDate, passportData, contactPhone, email);
                readers.add(reader);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка при чтении файла читателей.");
            e.printStackTrace();
        }
    }

    public static BookTransaction issueBook(List<Book> books, List<Reader> readers) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID книги, которую хотите выдать:");
        int bookId = scanner.nextInt();
        Book selectedBook = null;
        for (Book book : books) {
            if (book.book_id == bookId) {
                selectedBook = book;
                break;
            }
        }
        if (selectedBook == null) {
            System.out.println("Книга с таким ID не найдена.");
            return null;
        }
        System.out.println("Введите ID читателя, которому хотите выдать книгу:");
        int readerId = scanner.nextInt();
        Reader selectedReader = null;
        for (Reader reader : readers) {
            if (reader.getReaderId() == readerId) {
                selectedReader = reader;
                break;
            }
        }
        if (selectedReader == null) {
            System.out.println("Читатель с таким ID не найден.");
            return null;
        }
        System.out.println("Введите дату выдачи книги (в формате ДД.ММ.ГГГГ):");
        String issueDate = scanner.next();
        System.out.println("Введите дату возврата книги (в формате ДД.ММ.ГГГГ):");
        String dueDate = scanner.next();
        BookTransaction transaction = new BookTransaction(transactions.size() + 1, selectedBook.book_id, selectedReader.getReaderId(), issueDate, dueDate);
        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            writer.write(transaction.getTransactionId() + ",");
            writer.write(transaction.book_id + ",");
            writer.write(transaction.reader_id + ",");
            writer.write(transaction.issue_date + ",");
            writer.write(transaction.due_date + ",");
            writer.write(transaction.return_date + "\n");
            System.out.println("Книга успешно выдана читателю.");
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл.");
            e.printStackTrace();
        }
        return transaction;
    }

    public static void returnBook(List<BookTransaction> transactions) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID транзакции для возврата книги:");
        int transactionId = scanner.nextInt();

        // Загружаем транзакции из файла перед проверкой
        List<BookTransaction> fileTransactions = new ArrayList<>();
        readTransactionsFromFile(fileTransactions);

        BookTransaction selectedTransaction = null;
        for (BookTransaction transaction : fileTransactions) {
            if (transaction.getTransactionId() == transactionId) {
                selectedTransaction = transaction;
                break;
            }
        }

        if (selectedTransaction == null) {
            System.out.println("Транзакция с таким ID не найдена.");
            return;
        }

        if (!selectedTransaction.getReturnDate().isEmpty()) {
            System.out.println("Книга уже была возвращена.");
            return;
        }

        System.out.println("Введите дату возврата книги (в формате ДД.ММ.ГГГГ):");
        String returnDate = scanner.next();
        selectedTransaction.setReturnDate(returnDate);

        // Обновляем файл с транзакциями
        try {
            FileWriter writer = new FileWriter("transactions.txt");
            for (BookTransaction transaction : fileTransactions) {
                writer.write(transaction.getTransactionId() + ",");
                writer.write(transaction.book_id + ",");
                writer.write(transaction.reader_id + ",");
                writer.write(transaction.issue_date + ",");
                writer.write(transaction.due_date + ",");
                writer.write(transaction.getReturnDate() + "\n");
            }
            writer.close();
            System.out.println("Книга успешно возвращена.");
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл.");
            e.printStackTrace();
        }
    }

    private static void readTransactionsFromFile(List<BookTransaction> transactions) {
        try {
            File file = new File("transactions.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    // Пропускаем строки с недостаточным количеством полей
                    continue;
                }
                int transactionId = Integer.parseInt(parts[0]);
                int bookId = Integer.parseInt(parts[1]);
                int readerId = Integer.parseInt(parts[2]);
                String issueDate = parts[3];
                String dueDate = parts[4];
                String returnDate = parts.length > 5 ? parts[5] : "";
                BookTransaction transaction = new BookTransaction(transactionId, bookId, readerId, issueDate, dueDate);
                transaction.setReturnDate(returnDate);
                transactions.add(transaction);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка при чтении файла транзакций.");
            e.printStackTrace();
        }
    }
}