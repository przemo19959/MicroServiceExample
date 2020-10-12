package pl.dabrowski.MicroService2.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.dabrowski.MicroService2.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

}
