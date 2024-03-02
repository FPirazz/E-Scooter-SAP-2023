package sap.escooters.domain.entities;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class Ride {

	private Date startedDate;
	private Optional<Date> endDate;
	private User user;
	private EScooter scooter;
	private boolean ongoing;
	private String id;
	
	public Ride(User user, EScooter scooter) {
		this.id = UUID.randomUUID().toString();
		this.startedDate = new Date();
		this.endDate = Optional.empty();
		this.user = user;
		this.scooter = scooter;
		ongoing = true;
	}
	
	public String getId() {
		return id;
	}
	
	public void end() {
		endDate = Optional.of(new Date());
		ongoing = false;
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public boolean isOngoing() {
		return this.ongoing;
	}
	
	public Optional<Date> getEndDate() {
		return endDate;
	}

	public User getUser() {
		return user;
	}

	public EScooter getEScooter() {
		return scooter;
	}
}