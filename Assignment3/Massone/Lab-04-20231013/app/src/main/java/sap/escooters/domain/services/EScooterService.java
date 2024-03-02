package sap.escooters.domain.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.entities.Location;
import sap.escooters.ports.input.EScooterUseCases;
import sap.escooters.ports.output.EScooterRepository;
import sap.escooters.ports.output.EScooterSerializer;

import java.util.Optional;

public class EScooterService implements EScooterUseCases {
    private EScooterRepository escooterRepository;
    private EScooterSerializer escooterSerializer;

    public EScooterService(EScooterRepository escooterRepository, EScooterSerializer escooterSerializer) {
        this.escooterRepository = escooterRepository;
        this.escooterSerializer = escooterSerializer;
    }

    @Override
    public void registerNewEScooter(String id) {
        EScooter escooter = new EScooter(id, escooterRepository);
        escooter.save();
    }

    @Override
    public JsonObject getEScooterInfo(String id) {
        Optional<EScooter> escooter = escooterRepository.findById(id);
        if (escooter.isPresent()) {
            return escooterSerializer.toJson(escooter.get());
        } else {
            throw new RuntimeException("EScooter not found");
        }
    }

    public void updateEScooterState(String id, EScooter.EScooterState state) {
        Optional<EScooter> escooter = escooterRepository.findById(id);
        if (escooter.isPresent()) {
            escooter.get().updateState(state);
            escooter.get().save();
        } else {
            throw new RuntimeException("EScooter not found");
        }
    }

    public void updateEScooterLocation(String id, Location newLoc) {
        Optional<EScooter> escooter = escooterRepository.findById(id);
        if (escooter.isPresent()) {
            escooter.get().updateLocation(newLoc);
            escooter.get().save();
        } else {
            throw new RuntimeException("EScooter not found");
        }
    }
}