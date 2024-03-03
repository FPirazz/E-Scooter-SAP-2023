package sap.escooters.ports.input;

public interface RideUseCases {
    String startNewRide(String userId, String escooterId) throws Exception;
    String getRideInfo(String id) throws Exception;
    void endRide(String rideId) throws Exception;
    int getNumberOfOngoingRides();
}