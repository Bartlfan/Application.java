package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Diagnose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiagnoseRepo extends JpaRepository<Diagnose,Integer> {
    @Query
            ("select c from Diagnose c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%'))")
    List<Diagnose> search2(@Param("searchTerm") String searchTerm);
}
