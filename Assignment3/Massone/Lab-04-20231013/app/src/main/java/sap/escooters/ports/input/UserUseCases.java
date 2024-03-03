package sap.escooters.ports.input;

public interface UserUseCases {
    void registerNewUser(String id, String name, String surname) throws Exception;
    String getUserInfo(String id) throws Exception;
}