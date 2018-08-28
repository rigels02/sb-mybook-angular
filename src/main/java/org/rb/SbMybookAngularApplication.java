package org.rb;

import java.text.SimpleDateFormat;

import org.rb.entity.Book;
import org.rb.repo.IBooks;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SbMybookAngularApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbMybookAngularApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner initBookDB(IBooks repo) {
		
		return new CommandLineRunner() {
			
			@Override
			public void run(String... args) throws Exception {
				System.out.println("Init Books DB ....");
				
				SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
				
				for(int i=1; i<= 3; i++) {
					repo.save(new Book("Title_"+i,"Author_"+i,100+i*10,sf.parse("2018/03/23"+i)));
				}
			}
		};
	}
	
}
