package sap.escooters.domain.services;

import org.junit.jupiter.api.Test;

import sap.escooters.adapters.infrastructure.EScooterSerializerImpl;
import sap.escooters.adapters.infrastructure.db.EScooterRepositoryImpl;
import sap.escooters.adapters.mappers.EScooterSerializer;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.repositories.EScooterRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EscooterTest {
    @Test
    void test_createNewEScooter() {
        // Arrange
        String id = "123";
        EScooterRepository escooterRepository = mock(EScooterRepository.class);
        EScooterSerializer escooterSerializer = mock(EScooterSerializer.class);
        EScooterService eScooterService = new EScooterService(escooterRepository, escooterSerializer);

        when(escooterRepository.findById(id)).thenReturn(Optional.of(new EScooter(id)));

        // Act
        eScooterService.registerNewEScooter(id);

        // Assert
        assertTrue(eScooterService.escooterExists(id));
    }

        // saves a valid EScooter object
    @Test
    void test_save_valid_EScooter() {
        // Arrange
        EScooterRepository repository = new EScooterRepositoryImpl("dbase", new EScooterSerializerImpl());
        EScooter escooter = new EScooter("123");
    
        // Act
        repository.save(escooter);
        Optional<EScooter> savedEScooter = repository.findById("123");
    
        // Print
        System.out.println("Saved EScooter: " + savedEScooter);
    

        // Assert
        assertTrue(savedEScooter.isPresent());
        assertEquals(escooter, savedEScooter.get());
    }
}