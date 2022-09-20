package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Mitarbeiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MitarbeiterRepo extends JpaRepository<Mitarbeiter, Integer>{
    @Query("select c from Mitarbeiter c " +
            "where lower(c.firstname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.lastname) like lower(concat('%', :searchTerm, '%'))")


    List<Mitarbeiter> search2(@Param("searchTerm") String searchTerm);
}
