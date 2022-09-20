package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Krankenkasse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KrankenkasseRepo extends JpaRepository<Krankenkasse, Integer> {
    @Query("select c from Krankenkasse c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%'))")
    List<Krankenkasse> search2(@Param("searchTerm") String searchTerm);
}
