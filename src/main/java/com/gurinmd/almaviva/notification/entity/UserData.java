package com.gurinmd.almaviva.notification.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "chat_id")
    private Long chatId;
    
    @Column(name = "status_code")
    private String statusCode;
    
    @Column(name = "status_description")
    private String statusDescription;
    
    @Column(name = "folder")
    private String folder;
    
    @Column(name = "passport")
    private String passport;
    
    @Column(name = "last_check")
    private Instant lastCheck;

}
