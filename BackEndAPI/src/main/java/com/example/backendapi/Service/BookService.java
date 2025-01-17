package com.example.backendapi.Service;

import com.example.backendapi.Abstractions.IBookService;
import com.example.backendapi.Abstractions.IFileStorageService;
import com.example.backendapi.Model.Book;
import com.example.backendapi.Model.BookImage;
import com.example.backendapi.Model.PartFileModel;
import com.example.backendapi.Model.User;
import com.example.backendapi.ModelMapping.BookMapping;
import com.example.backendapi.ModelMapping.BookModel;
import com.example.backendapi.ModelMapping.ExchangeBook;
import com.example.backendapi.ModelMapping.PagingModel;
import com.example.backendapi.Repository.BookImageRepository;
import com.example.backendapi.Repository.BookRepository;
import com.example.backendapi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Service
@RequiredArgsConstructor
public class BookService implements IBookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private IFileStorageService fileStorageService;
    @Autowired
    private BookImageRepository bookImageRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public boolean postBook(UUID userId, BookModel bookModel) {
        Book book = new Book();
        UUID id = UUID.randomUUID();
        var user = userRepository.findById(userId);
        if (book != null) {
            book.setId(id);
            book.setCreatedDate(new Date(System.currentTimeMillis()));
            book.setAuthor(bookModel.getAuthor());
            book.setDescription(bookModel.getDescription());
            book.setName(bookModel.getName());
            book.setContactPhone(bookModel.getContactPhone());
            book.setExchange(false);
            book.setUser(user.get());
            bookRepository.save(book);
            if (bookModel.getProductImages() != null) {
                addImageBook(id, bookModel.getProductImages());
            }
            return true;
        }
        return false;
    }

    @Override
    public PagingModel<BookModel> getAllBook(String keyword, int page, int size, String sortType, String sortBy, String mostRecent) {
        Sort sort;
        if (sortType != null && sortType.equals("desc")) {
            sort =  Sort.by(Sort.Order.desc(sortBy));
        } else {
            sort =  Sort.by(Sort.Order.asc(sortBy));
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Book> listProducts = bookRepository.findAll(sort);
        if (keyword != null) {
            listProducts = listProducts.stream().filter(product ->
                            product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                                    product.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }
        if (mostRecent.equals("asc")) {
            listProducts.sort((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));
        }
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), listProducts.size());
        final Page<Book> pageList = new PageImpl<>(listProducts.subList(start, end), pageable, listProducts.size());
        var result = BookMapping.toListBook(pageList.getContent());
        int totalPage = (int) Math.ceil((double) listProducts.size() / size);
        int totalItem = listProducts.size();
        return new PagingModel<>(result, size, totalItem, page, totalPage);
    }

    @Override
    public BookModel getBookByID(UUID id) {
        Optional<Book> book = bookRepository.findById(id);
        if (!book.isPresent()){
            return null;
        }
        return BookMapping.toBook(book.get());
    }

    @Override
    public ExchangeBook exchangeBook(UUID id, String userName) {
        //Book giver
        Book book = bookRepository.findById(id).get();
        User userGiverBook = book.getUser();
        //book recipient
        User userBookRecipient = userRepository.findByUserName(userName);
        //
        ExchangeBook exchangeBook = new ExchangeBook();
        //set info book recipient
        exchangeBook.setEmailBookRecipient(userBookRecipient.getEmail());
        exchangeBook.setPhoneNumberBookRecipient(userBookRecipient.getPhoneNumber());
        exchangeBook.setUserNameBookRecipient(userBookRecipient.getUsername());
        //set info book giver
        exchangeBook.setNameBook(book.getName());
        exchangeBook.setEmailBookGiver(userGiverBook.getEmail());
        exchangeBook.setUserNameBookGiver(userGiverBook.getUsername());

        return exchangeBook;
    }

    private void addImageBook(UUID bookId, List<MultipartFile> productImage) {
        Book product = bookRepository.findById(bookId).get();
        if (productImage == null) {
            return;
        }
        for (MultipartFile file : productImage) {
            String path = fileStorageService.saveImage(file);
            BookImage bookImage = new BookImage();
            bookImage.setBook(product);
            bookImage.setImage(path);
            bookImage.setCreatedDate(new Date(System.currentTimeMillis()));
            bookImage.setId(UUID.randomUUID());
            bookImageRepository.save(bookImage);
        }
    }

    public void addImage(UUID bookId, List<PartFileModel> productImage) {
        Book product = bookRepository.findById(bookId).get();
        if (productImage == null) {
            return;
        }
        for (PartFileModel file : productImage) {
            String path = fileStorageService.saveImage(file.getFile());
            BookImage bookImage = new BookImage();
            bookImage.setBook(product);
            bookImage.setImage(path);
            bookImage.setCreatedDate(new Date(System.currentTimeMillis()));
            bookImage.setId(UUID.randomUUID());
            bookImageRepository.save(bookImage);
        }
    }
}
