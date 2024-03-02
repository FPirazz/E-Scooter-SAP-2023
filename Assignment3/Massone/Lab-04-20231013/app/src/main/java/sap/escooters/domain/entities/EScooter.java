package sap.escooters.domain.entities;

import java.util.Optional;

public class EScooter  {

    private String id;
    public enum EScooterState { AVAILABLE, IN_USE, MAINTENANCE}    
    private EScooterState state;
    private Optional<Location> loc;
    
    public EScooter(String id) {
        this.id = id;
        this.state = EScooterState.AVAILABLE;
        this.loc = Optional.empty();
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

    public void setState(EScooterState state) {
        this.state = state;
    }
    
    public void setLocation(Location newLoc) {
        loc = Optional.of(newLoc);
    }
	
	public Optional<Location> getCurrentLocation(){
		return loc;
	}
}
