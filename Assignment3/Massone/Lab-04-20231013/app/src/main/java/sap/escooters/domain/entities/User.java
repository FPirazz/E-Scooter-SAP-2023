package sap.escooters.domain.entities;

import java.util.UUID;

public class User {

	private String id;
	private String name;
	private String surname;
	
	public User(String name, String surname) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.surname = surname;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
}