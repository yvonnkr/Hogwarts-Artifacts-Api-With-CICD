package com.yvolabs.hogwartsartifactsapi.hogwartsuser;

import java.util.List;

/**
 * @author Yvonne N
 */
public interface UserService {
    List<HogwartsUser> findAll();

    HogwartsUser save(HogwartsUser user);

    HogwartsUser findById(Integer userId);

    HogwartsUser update(Integer userId, HogwartsUser update);

    void delete(Integer userId);
}
