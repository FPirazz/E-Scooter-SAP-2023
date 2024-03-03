package sap.escooters.ports.input;

public interface EScooterUseCases {
    void registerNewEScooter(String id);
    String getEScooterInfo(String id);
}