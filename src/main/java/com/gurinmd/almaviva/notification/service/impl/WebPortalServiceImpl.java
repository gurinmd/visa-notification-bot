package com.gurinmd.almaviva.notification.service.impl;

import javax.annotation.PostConstruct;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.gurinmd.almaviva.notification.dto.CheckStatusResult;
import com.gurinmd.almaviva.notification.dto.WebPortalResponseDto;
import com.gurinmd.almaviva.notification.service.WebPortalService;
import com.gurinmd.almaviva.notification.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class WebPortalServiceImpl implements WebPortalService {
    
    private static final String STATUS_FIELD_CODE = "status";
    
    private static final String CITY_JSON_POINTER = "/group/site/name";
    
    private Map<String, String> RESPONSE_CODE_MESSAGES_CODES;
    private Map<String, String> MESSAGE_CODES_DESCRIPTION;
    
    private static final String QUERY_STATUS_ENDPOINT_MASK = 
        "https://ru.almaviva-visa.services/api/folders/by-folder-id-and-person-passport?folderId=%s&personPassport=%s";

    private HttpClient httpClient = HttpClient.newBuilder().build();
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public WebPortalResponseDto getStatus(String passport, String folder) {
        WebPortalResponseDto res = new WebPortalResponseDto();
        try {
            HttpRequest request = HttpRequest
                .newBuilder()
                .uri(new URI(buildUrl(passport, folder)))
                .GET().build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            String responseString = response.body();
            
            res = parseResponse(responseString);
        } catch (Exception exception) {
            log.error(exception.getLocalizedMessage());
            res.setStatusCode(Constants.UNKNOWN_STATUS_CODE);
        }
        return res;
    }

    @Override
    public CheckStatusResult getStatusResultData(String passport, String folder) {
        WebPortalResponseDto webResponse = getStatus(passport, folder);
        CheckStatusResult result = buildResult(webResponse);
        result.setFolder(folder);
        result.setPassport(passport);
        return result;
    }
    
    @PostConstruct
    public void initMessageMaps() {
        RESPONSE_CODE_MESSAGES_CODES = ImmutableMap.<String, String> builder()
            .put("CREATED", "CREATED")
            .put("RECEIVED", "CREATED")
            .put("CASHOUT_RECEPTION", "CREATED")
            .put("CASHOUT_WAITING", "CREATED")
            .put("WAITING_ROOM", "CREATED")
            .put("TREATMENT", "TREATMENT")
            .put("CALLED", "TREATMENT")
            .put("COMPLIANT_QUALITY", "TREATMENT")
            .put("COMPLIANT_VALIDITY", "TREATMENT")
            .put("NON_COMPLIANT_QUALITY", "TREATMENT")
            .put("NON_COMPLIANT_VALIDITY", "TREATMENT")
            .put("DELIVERED_TO_HUB", "OS_SP_HUB")
            .put("RECEIVED_IN_HUB", "IS_HUB_SP")
            .put("DELIVERED_EMBASSY", "OS_HUB_CONS")
            .put("ADD_REC_DELIVERED", "OS_HUB_CONS")
            .put("ADD_REC_RECEPTION", "OS_HUB_CONS")
            .put("EMBASSY_RECEIVED", "IS_CONS")
            .put("EMBASSY_DELIVERED_CENTER", "OS_CONS_HUB")
            .put("EMBASSY_RETURNED", "IS_HUB")
            .put("DELIVERED_TO_SITE", "OS_HUB_SP")
            .put("RECEIVED_FROM_HUB", "IS_SP")
            .put("DELIVERED_CLIENT", "OS_SP_CLI")
            .put("PACKAGE_DELIVERED", "OS_HUB_CR")
            .build();

        MESSAGE_CODES_DESCRIPTION = ImmutableMap.<String, String> builder()
            .put("CREATED", "Ваша встреча создана")
            .put("IS_HUB_SP", "Ваши документы прибыли в центральный операционный офис")
            .put("IS_CONS", "Ваши документы переданы в Генеральное Консульство Италии в Москве")
            .put("OS_CONS_HUB", "Ваш паспорт готов к отправке в Визовый центр в городе %s")
            .put("IS_SP", "Ваш паспорт готов к выдаче в Визовом центре в городе %s")
            .put("TREATMENT", "Ваше заявление на визу обрабатывается в Визовом центре в городе %s")
            .put("OS_SP_HUB", "Ваши документы находятся в пути в центральный операционный офис")
            .put("OS_HUB_SP", "Ваш паспорт находится в пути в Визовый центр в городе %s")
            .put("OS_SP_CLI", "Ваши документы получены в Визовом центре в городе %s")
            .put("OS_HUB_CR", "Ваш паспорт передан в курьерскую службу")
            .put("OS_HUB_CONS", "Ваши документы прибыли в центральный операционный офис")
            .put("IS_HUB", "Ваш паспорт готов к отправке в Визовый центр в городе %s")
            .build();
    }

    private String buildUrl(String passport, String folder) {
        return String.format(QUERY_STATUS_ENDPOINT_MASK, folder, passport);
    }
    
    private WebPortalResponseDto parseResponse(String response) {
        WebPortalResponseDto res = new WebPortalResponseDto();
        try {
            JsonNode responseTree = objectMapper
                .readTree(response);
            JsonNode statusNode = responseTree.get(STATUS_FIELD_CODE);
            res.setStatusCode(statusNode.asText());
            JsonNode cityNode = responseTree.at(CITY_JSON_POINTER);
            res.setCity(cityNode.asText());
        } catch (Exception ex) {
            log.warn(ex.getLocalizedMessage());
            res.setStatusCode(Constants.UNKNOWN_STATUS_CODE);
        }
        return res;
    }
    
    private CheckStatusResult buildResult(WebPortalResponseDto responseDto) {
        String responseCode = responseDto.getStatusCode();
        
        CheckStatusResult res = new CheckStatusResult();
        res.setStatusCode(responseCode);
        
        String message = Optional.ofNullable(RESPONSE_CODE_MESSAGES_CODES.get(responseCode))
            .map(s -> MESSAGE_CODES_DESCRIPTION.get(s))
            .orElse("");
        
        String formattedMessage = String.format(message, responseDto.getCity());
        res.setStatusDescription(formattedMessage);
        res.setCheckedAt(Instant.now());
        return res;
        
    }
}
