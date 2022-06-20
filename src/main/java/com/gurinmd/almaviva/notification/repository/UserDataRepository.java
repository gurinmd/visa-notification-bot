package com.gurinmd.almaviva.notification.repository;

import javax.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

import com.gurinmd.almaviva.notification.entity.UserData;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserDataRepository extends PagingAndSortingRepository<UserData, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<UserData> findOneByUserIdAndPassportAndFolder(Long userId, String passport, String folder);
    
    @Transactional
    void deleteByUserId(Long userId);
    
    List<UserData> findAllByUserId(Long userId);
}
