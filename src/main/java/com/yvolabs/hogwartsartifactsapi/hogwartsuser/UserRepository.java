package com.yvolabs.hogwartsartifactsapi.hogwartsuser;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yvonne N
 */
public interface UserRepository extends JpaRepository<HogwartsUser, Integer> {
}
