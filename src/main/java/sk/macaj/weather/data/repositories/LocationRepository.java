package sk.macaj.weather.data.repositories;

import org.springframework.data.repository.CrudRepository;
import sk.macaj.weather.data.entities.Location;

public interface LocationRepository extends CrudRepository<Location, Long> {

    Location findByName(String name);

}
