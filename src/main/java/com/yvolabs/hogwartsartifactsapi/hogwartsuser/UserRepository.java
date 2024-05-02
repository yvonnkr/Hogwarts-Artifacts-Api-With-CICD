package com.yvolabs.hogwartsartifactsapi.hogwartsuser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Yvonne N
 */
public interface UserRepository extends JpaRepository<HogwartsUser, Integer> {

    Optional<HogwartsUser> findByUsername(String username);
}
