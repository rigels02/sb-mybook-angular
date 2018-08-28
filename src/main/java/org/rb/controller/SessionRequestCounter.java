package org.rb.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value=WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionRequestCounter {

	int counter;

	public int getCounter() {
		return counter;
	}
	
	public void increment() {counter++; }
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	
}
