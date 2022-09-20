package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.VersicherungsArt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersicherungsArtRepo extends JpaRepository<VersicherungsArt, Integer> {
}
