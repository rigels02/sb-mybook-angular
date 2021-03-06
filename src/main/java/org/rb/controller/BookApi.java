package org.rb.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rb.entity.Book;
import org.rb.repo.IBooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class BookApi {

	private String uploadRootPath;

	@Autowired
	IBooks repo;
	
	@Autowired
	private SessionRequestCounter sessionRequestCounter;

	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

	@GetMapping(path = "/books")
	public Iterable<Book> getBooks() {
		System.out.println("getBooks....");
		System.out.println("Sessions ID: "+
		RequestContextHolder.currentRequestAttributes().getSessionId()
		);
		System.out.println("PutEditCounter= "+sessionRequestCounter.getCounter());
		sessionRequestCounter.increment();
		
		return repo.findAll();
	}

	/**
	 * Save the new book fields received by multipart/form-data request
	 * @param file
	 * @param id
	 * @param title
	 * @param author
	 * @param pages
	 * @param request
	 * @return
	 */
	@PostMapping(path = "/books")
	public ResponseEntity<HttpStatus> postBook(
			@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("id") long id, @RequestParam("title") String title,
			@RequestParam("author") String author,
			@RequestParam("pages") int pages, 
			@RequestParam("publishDate") String pubDate,
			HttpServletRequest request) {
		
		
		Book book;
		try {
			book = new Book(title, author, pages, sf.parse(pubDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		book.setId(id);
		System.out.println("Post: " + book);
		MultipartFile[] fileDatas = new MultipartFile[1];
		fileDatas[0] = file;

		putBookImage(request, book, fileDatas);

		repo.save(book);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping(path = "/books/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Book getBookById(@PathVariable("id") long id) {
		System.out.println("getBookById: " + id);
		Optional<Book> book = repo.findById(id);
		return book.get();

	}

	/**
	 * Save existing book (with id) fields received by multipart/form-data request
	 * @param request
	 * @param id
	 * @param file
	 * @param title
	 * @param author
	 * @param pages
	 * @return
	 */
	@PutMapping(path = "/books/{id}")
	public Book putBookById(HttpServletRequest request, 
			@PathVariable("id") long id,
			@RequestParam(name = "file", required = false) MultipartFile file, 
			@RequestParam("title") String title,
			@RequestParam("author") String author, 
			@RequestParam("pages") int pages,
			@RequestParam("publishDate") String pubDate) {

		
		Book newBook;
		try {
			newBook = new Book(title, author, pages,sf.parse(pubDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		newBook.setId(id);
		System.out.println("putBookById: " + newBook);

		Optional<Book> obook = repo.findById(id);
		Book book = obook.get();
		book.makeCopyFrom(newBook);
		putBookImage(request, book, file);
		

		return repo.save(book);
	}

	@DeleteMapping(path = "/books/{id}")
	public ResponseEntity<HttpStatus> deleteBookById(@PathVariable("id") long id) {

		System.out.println("deleteBookById: " + id);
		Optional<Book> obook = repo.findById(id);
		Book book = obook.get();
		repo.delete(book);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/books/imageV/{id}")
	public void getImage(@PathVariable("id") long id, HttpServletResponse response) {

		Optional<Book> book = repo.findById(id);
		if (!book.isPresent()) {
			System.err.println("Not found...");
			return;

		}
		byte[] image = book.get().getCoverImage();

		if (image == null)
			return;

		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		try {
			ServletOutputStream os = response.getOutputStream();
			os.write(image);
			os.close();
			System.out.println("Image sent...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ---------------Helpers-------------------//

	private synchronized void putBookImage(HttpServletRequest request, Book book, MultipartFile[] fileDatas) {

		doFileUpload(request, fileDatas);

		book.setCoverImage(getUploadedImage());
	}

	private synchronized void putBookImage(HttpServletRequest request, Book book, MultipartFile fileData) {

		doFileUpload(request, fileData);

		book.setCoverImage(getUploadedImage());
	}

	private void doFileUpload(HttpServletRequest request, MultipartFile fileData) {
		if (fileData == null)
			return;
		MultipartFile[] datas = new MultipartFile[1];
		datas[0] = fileData;
		doFileUpload(request, datas);

	}

	private void doFileUpload(HttpServletRequest request, MultipartFile[] fileDatas) {

		if (fileDatas == null)
			return;
		// Root Directory.
		uploadRootPath = request.getServletContext().getRealPath("upload");
		System.out.println("uploadRootPath=" + uploadRootPath);

		File uploadRootDir = new File(uploadRootPath);
		// Create directory if it not exists.
		if (!uploadRootDir.exists()) {
			uploadRootDir.mkdirs();
		}

		int fileIdx=0;
		for (MultipartFile fileData : fileDatas) {
			if (fileData == null)
				return;
			File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + "image"+fileIdx+".png");
			fileIdx++;
			// stream out

			try {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(fileData.getBytes());
				stream.close();
			} catch (Exception e) {

				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * Only one image file used!
	 * @return
	 */
	private byte[] getUploadedImage() {
		if (uploadRootPath == null)
			return null;
		File imgDir = new File(uploadRootPath);
		if (!imgDir.exists())
			return null;

		Path path = Paths.get(imgDir.getAbsolutePath() + File.separator + "image0.png");
		if (!Files.exists(path))
			return null;

		try {
			byte[] imageBytes = Files.readAllBytes(path);
			path.toFile().delete();
			if (imageBytes.length == 0)
				return null;

			return imageBytes;
		} catch (IOException e) {

			e.printStackTrace();
			System.err.println(e.getMessage());
		}

		return null;
	}

}
