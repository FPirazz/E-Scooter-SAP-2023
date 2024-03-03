package sap.escooters.adapters.mappers;

import sap.escooters.domain.entities.EScooter;

public interface EScooterSerializer {
    String serialize(EScooter escooter);
    EScooter deserialize(String escooterData);
}