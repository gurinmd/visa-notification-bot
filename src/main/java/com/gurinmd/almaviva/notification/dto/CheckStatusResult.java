package com.gurinmd.almaviva.notification.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class CheckStatusResult {
    /**
     * Status code, received from web portal.
     */
    private String statusCode;

    /**
     * Status code description.
     */
    private String statusDescription;

    /**
     * When status was checked.
     */
    private Instant checkedAt;
    
    /**
     * City of application
     */
    private String city;

    /**
     * Passport
     */
    private String passport;

    /**
     * Reg number
     */
    private String folder;
}
