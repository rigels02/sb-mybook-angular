package org.rb.repo;

import org.rb.entity.Book;
import org.springframework.data.repository.CrudRepository;

public interface IBooks extends CrudRepository<Book, Long>{

}
