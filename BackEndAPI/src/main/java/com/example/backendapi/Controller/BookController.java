package com.example.backendapi.Controller;

import com.example.backendapi.Abstractions.IBookService;
import com.example.backendapi.Abstractions.IFileStorageService;
import com.example.backendapi.Extensions.FileUploadModelConverter;
import com.example.backendapi.Model.Book;
import com.example.backendapi.Model.PartFileModel;
import com.example.backendapi.Model.User;
import com.example.backendapi.ModelMapping.BookMapping;
import com.example.backendapi.ModelMapping.BookModel;
import com.example.backendapi.ModelMapping.ExchangeBook;
import com.example.backendapi.ModelMapping.PagingModel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {
    @Autowired
    private IBookService bookService;
    @Autowired
    private IFileStorageService fileStorageService;



    @PostMapping("/post")
    public ResponseEntity<Boolean> post(@ModelAttribute BookModel book) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean result = bookService.postBook(user.getId(), book);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<PagingModel<BookModel>> getAll(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                         @RequestParam(defaultValue = "") String search,
                                                         @RequestParam(defaultValue = "name") String sortBy,
                                                         @RequestParam(defaultValue = "asc") String sortType,
                                                         @RequestParam(defaultValue = "desc") String mostRecent) {
        try {
            PagingModel<BookModel> books = bookService.getAllBook(search, page, pageSize, sortType, sortBy, mostRecent);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}")
    public BookModel getBookByID(@PathVariable UUID id){
        return bookService.getBookByID(id);
    }
    @PostMapping("/exchange")
    public ExchangeBook exchange(@RequestBody UUID id, Authentication auth){
        String userName = auth.getName();

        return bookService.exchangeBook(id,userName);
    }
}
