package org.example.Service;

import org.example.DTO.Book;
import org.example.DTO.BookOrderInformation;
import org.example.DTO.StudentBook;
import org.example.DTO.User;
import org.example.Enums.Status;
import org.example.Repository.BookRepository;
import org.example.Repository.StudentBookRepository;
import org.example.Util.ScannerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentService {
    @Autowired
    public BookRepository bookRepository;
    @Autowired
    private StudentBookRepository studentBookRepository;
    public void bookList() {
        List<Book> bookList = bookRepository.getBookList();
        for(Book b : bookList) if (b.isVisible() && b.getAmount() > 0) System.out.println("Book ( id ='" + b.getId() + "', title ='" + b.getTitle() + "', author ='" + b.getAuthor() + "' );");
    }
    public void takeBook(User user) {
        int count = studentBookRepository.getOrderInfoListByStudentId(user.getId()).size();
        System.out.print("Enter Id : ");
        Book book = bookRepository.getBookById(ScannerUtil.scannerInt.nextInt());
        if (book == null){
            System.out.println("Sorry, the book you were looking for does not exist. \nPlease, try again");
            return;
        } else if (count > 5) {
            System.out.println("Sorry, you can't get more than 5 books!");
            return;
        }
        StudentBook studentBook = new StudentBook();
        studentBook.setBookId(book.getId());
        studentBook.setStudentId(user.getId());
        studentBook.setStatus(Status.TAKEN);
        studentBook.setReturnedDate(LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now() ;
        studentBook.setDuration(localDateTime.plusDays(30));
        getResult(studentBookRepository.save(studentBook));
        book.setAmount(book.getAmount() - 1);
        bookRepository.updateBook(book.getId(),book);
        System.out.println("You got the book for 30 days!\n\n");
    }
    public void takenBook(User user) {
        // OrderNumber  BookTitle  BookAuthor TakenTime
        List<BookOrderInformation> informations = studentBookRepository.getOrderInfoListByStudentId(user.getId());
        for (BookOrderInformation i : informations) System.out.println("Book (Order number ='" + i.getSb_id() + "', Book title ='" + i.getBook_title() +
                "', Book author ='" + i.getBook_author() + "', Taken date ='" + i.getTaken_time() + "' );");
    }
    public void returnBook() {
        System.out.print("Enter Id : ");
        Book book = bookRepository.getBookById(ScannerUtil.scannerInt.nextInt());
        if (book == null){
            System.out.println("Sorry, the book you were looking for does not exist. \nPlease, try again");
            return;
        }
        StudentBook studentBook = studentBookRepository.getStudentBookByBookId(book.getId());
        studentBook.setStatus(Status.RETURNED);
        studentBook.setReturnedDate(LocalDateTime.now());
        getResult(studentBookRepository.updateStudentBook(studentBook.getId(),studentBook));
        book.setAmount(book.getAmount() + 1);
        bookRepository.updateBook(book.getId(),book);
    }
    public void history() {
        List<BookOrderInformation> informations= studentBookRepository.getHistory();
        for (BookOrderInformation i : informations) System.out.println("Book (Order number ='" + i.getSb_id() + "', Book title ='" + i.getBook_title() +
                "', Book author ='" + i.getBook_author() + "', Taken date ='" + i.getTaken_time() + "', Returned date ='" + i.getReturned_time() + "' );");

    }
    public void orderBook() {
        ///
    }

    public void getResult(int n){
        if (n == 1) System.out.println("successfully!");
        else System.out.println("failed!");
    }


}
