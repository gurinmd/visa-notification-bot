package com.gurinmd.almaviva.notification.service;

import com.gurinmd.almaviva.notification.dto.CheckStatusResult;
import com.gurinmd.almaviva.notification.dto.WebPortalResponseDto;

/**
 * Service to interact with web site
 */
public interface WebPortalService {

    /**
     * Requests portal to get the current status
     * @param passport passport
     * @param folder folder
     * @return respone data object
     */
    WebPortalResponseDto getStatus(String passport, String folder);

    /**
     * Requests WEB portal and adds description.
     * @param passport passport
     * @param folder folder
     * @return data object
     */
    CheckStatusResult getStatusResultData(String passport, String folder);
}
