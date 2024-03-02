package sap.escooters.domain.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.entities.Location;
import sap.escooters.domain.entities.EScooter.EScooterState;
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
        EScooter escooter = new EScooter(id);
        escooterRepository.save(escooter);
    }

    public void updateEScooterState(String id, EScooterState state) {
        Optional<EScooter> escooter = escooterRepository.findById(id);
        if (escooter.isPresent()) {
            escooter.get().setState(state);
            escooterRepository.save(escooter.get());
        } else {
            throw new RuntimeException("EScooter not found");
        }
    }

    public void updateEScooterLocation(String id, Location newLoc) {
        Optional<EScooter> escooter = escooterRepository.findById(id);
        if (escooter.isPresent()) {
            escooter.get().setLocation(newLoc);
            escooterRepository.save(escooter.get());
        } else {
            throw new RuntimeException("EScooter not found");
        }
    }

    public boolean escooterExists(String escooterId) {
        return escooterRepository.findById(escooterId).isPresent();
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
}