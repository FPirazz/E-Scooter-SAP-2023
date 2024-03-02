package sap.escooters.ports.input;

public interface HttpPort {
    void start();
    void registerNewUser(String id, String name, String surname);
    void getUserInfo(String userId);
    void registerNewEScooter(String id);
    void getEScooterInfo(String escooterId);
    void startNewRide(String userId, String escooterId);
    void getRideInfo(String rideId);
    void endRide(String rideId);
}