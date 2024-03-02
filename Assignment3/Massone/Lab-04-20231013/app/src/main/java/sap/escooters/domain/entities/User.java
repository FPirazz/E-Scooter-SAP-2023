package sap.escooters.domain.entities;

import io.vertx.core.json.JsonObject;
import sap.escooters.ports.output.UserRepository;

public class User {

	private String id;
	private String name;
	private String surname;
	private UserRepository userRepository;
	
	public User(String id, String name, String surname, UserRepository userRepository) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.userRepository = userRepository;
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
	
	public void save() {
		try {
			userRepository.save(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public JsonObject toJson() {
		JsonObject userObj = new JsonObject();
		userObj.put("id", id);
		userObj.put("name", name);
		userObj.put("surname", surname);            
		return userObj;
	}
}