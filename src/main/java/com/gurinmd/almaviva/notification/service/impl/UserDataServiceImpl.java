package com.gurinmd.almaviva.notification.service.impl;

import java.util.List;
import java.util.Optional;

import com.gurinmd.almaviva.notification.entity.UserData;
import com.gurinmd.almaviva.notification.repository.UserDataRepository;
import com.gurinmd.almaviva.notification.service.UserDataService;
import com.gurinmd.almaviva.notification.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserDataServiceImpl implements UserDataService {
    
    @Autowired
    private UserDataRepository userDataRepository;
    
    @Override
    @Transactional
    public UserData addUserData(Long userId, String userName, Long chatId, String folder, String passport) {
        UserData res = userDataRepository.findOneByUserIdAndPassportAndFolder(userId, passport, folder)
            .orElse(UserData.builder()
                .userId(userId)
                .userName(userName)
                .chatId(chatId)
                .folder(folder)
                .passport(passport)
                .statusCode(Constants.UNKNOWN_STATUS_CODE)
                .build());
        res = userDataRepository.save(res);
        return res;
    }

    @Override
    public void deleteUserData(Long userId) {
        userDataRepository.deleteByUserId(userId);
    }

    @Override
    public Iterable<UserData> findAll() {
        return userDataRepository.findAll();
    }

    @Override
    public void save(UserData userData) {
        userDataRepository.save(userData);
    }

    @Override
    public List<UserData> findByUserId(Long userId) {
        return userDataRepository.findAllByUserId(userId);
    }
}
