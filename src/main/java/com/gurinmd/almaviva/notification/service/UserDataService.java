package com.gurinmd.almaviva.notification.service;

import java.util.List;
import java.util.Optional;

import com.gurinmd.almaviva.notification.entity.UserData;

/**
 * Main business logic interface
 */
public interface UserDataService {
    
    UserData addUserData(Long userId, String userName, Long chatId, String folder, String passport);
    
    void deleteUserData(Long userId);

    Iterable<UserData> findAll();
    
    void save(UserData userData);
    
    List<UserData> findByUserId(Long userId);
}
