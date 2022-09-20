package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Arzt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArztRepo extends JpaRepository<Arzt, Integer> {

    @Query("select c from Arzt c " +
            "where lower(c.firstname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.lastname) like lower(concat('%', :searchTerm, '%'))")

    List<Arzt> search2(@Param("searchTerm") String searchTerm);

    @Query("SELECT a FROM Arzt a WHERE a.arztId NOT IN ?1")
    List<Arzt> notIn(List<Integer> aerzteId);
}
