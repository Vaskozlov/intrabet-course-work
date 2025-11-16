package org.vaskozlov.is.course.repository;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import org.vaskozlov.is.course.bean.User;

import java.util.Optional;

@Repository(dataStore = "IsCoursePU")
public interface UserRepository extends BasicRepository<User, Long> {
    @Find
    Optional<User> findByEmail(String email);

    @Find
    Optional<User> findByUsername(String username);

    @Find
    Optional<User> findByPasswordHash(String passwordHash);
}
