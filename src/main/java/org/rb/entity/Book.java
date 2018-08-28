package org.rb.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
public class Book implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String title;
	private String author;
	private int pages;
	
	@Temporal(TemporalType.DATE)
	private Date publishDate;
	
	@Lob
	private byte[] coverImage;
	
	public Book() {
		
	}

	public Book(String title, String author, int pages, Date pubDate) {
		
		this.title = title;
		this.author = author;
		this.pages = pages;
		this.publishDate = pubDate;
	}

	public void makeCopyFrom(Book other) {
		this.id = other.id;
		this.title = other.title;
		this.author = other.author;
		this.pages = other.pages;
		this.publishDate = other.publishDate;
		
	}
	

}
