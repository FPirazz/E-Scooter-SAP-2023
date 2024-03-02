package sap.escooters.domain.entities;

import java.util.Optional;

import sap.escooters.ports.output.EScooterRepository;

public class EScooter  {

    private String id;
    public enum EScooterState { AVAILABLE, IN_USE, MAINTENANCE}    
    private EScooterState state;
    private Optional<Location> loc;
    private EScooterRepository escooterRepository;
    
    public EScooter(String id, EScooterRepository escooterRepository) {
        this.id = id;
        this.state = EScooterState.AVAILABLE;
        this.loc = Optional.empty();
        this.escooterRepository = escooterRepository;
    }
    
	
	public String getId() {
		return id;
	}

	public EScooterState getState() {
		return state;
	}
	
	public boolean isAvailable() {
		return state.equals(EScooterState.AVAILABLE);
	}

	public void updateState(EScooterState state) {
		this.state = state;
		save();
	}
	
	public void updateLocation(Location newLoc) {
		loc = Optional.of(newLoc);
		save();
	}
	
	public Optional<Location> getCurrentLocation(){
		return loc;
	}
	
    public void save() {
        try {
            escooterRepository.save(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
}
