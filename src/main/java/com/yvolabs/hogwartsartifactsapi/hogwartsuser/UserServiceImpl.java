package com.yvolabs.hogwartsartifactsapi.hogwartsuser;

import com.yvolabs.hogwartsartifactsapi.system.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yvonne N
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<HogwartsUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public HogwartsUser save(HogwartsUser user) {
        return userRepository.save(user);
    }

    @Override
    public HogwartsUser findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    /**
     * We are not using this update to change user password.
     */
    @Override
    public HogwartsUser update(Integer userId, HogwartsUser update) {
        return userRepository.findById(userId)
                .map(foundUser -> {
                    foundUser.setUsername(update.getUsername());
                    foundUser.setEnabled(update.isEnabled());
                    foundUser.setRoles(update.getRoles());
                    return userRepository.save(foundUser);
                })
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    @Override
    public void delete(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        userRepository.deleteById(userId);
    }
}
