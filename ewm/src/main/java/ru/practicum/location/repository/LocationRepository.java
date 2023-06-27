package ru.practicum.location.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.location.model.Location;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findFirstByLatAndLon(Float lat, Float lon);

    List<Location> findAllByIdIn(List<Long> ids, Pageable pageable);

    @Query(value = "SELECT * FROM locations l " +
            "WHERE distance(l.lat,l.lon,:lat,:lon) <= :radius",
            nativeQuery = true)
    List<Location> getLocationIdsInRadius(@Param("lat") Float lat,
                                          @Param("lon") Float lon,
                                          @Param("radius") Float radius,
                                          Pageable pageable);
}
