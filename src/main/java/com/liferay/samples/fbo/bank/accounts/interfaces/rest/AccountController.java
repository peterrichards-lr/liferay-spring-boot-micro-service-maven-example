package com.liferay.samples.fbo.bank.accounts.interfaces.rest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@CrossOrigin
@RequestMapping("/account/api/v1")
public class AccountController {

    private static Logger LOG = LoggerFactory.getLogger(AccountController.class);
    @Value("${liferay.headless-api.base-url}")
    private String baseUrl;
    @Value("${liferay.headless-api.my-user-account}")
    private String myUserAccountUri;

    private WebClient buildWebClient() {
        final WebClient.Builder builder = WebClient.builder();

        return builder.baseUrl(
                        baseUrl
                ).defaultHeader(
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE

                ).defaultHeader(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private JSONObject getUserAccountDetails(Jwt jwt) {
        final WebClient webClient = buildWebClient();
        final String responseJson = webClient.get()
                .uri(myUserAccountUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (LOG.isDebugEnabled()) {
            LOG.debug("JWT user Id: " + jwt.getClaims().get("sub").toString());
            LOG.debug("Response JSON: " + responseJson);
        }

        return new JSONObject(responseJson);
    }

    private void logJwt(Jwt jwt) {
        if (LOG.isInfoEnabled()) {
            LOG.info("JWT Claims: " + jwt.getClaims());
            LOG.info("JWT ID: " + jwt.getId());
            LOG.info("JWT Subject: " + jwt.getSubject());
        }
    }

    /*
     * This method can only be called if a valid JWT token was provided
     * It returns the userId from the token
     */
    @GetMapping("/myroles")
    public ResponseEntity<String> getRoles(@AuthenticationPrincipal Jwt jwt, @CurrentSecurityContext SecurityContext context) {
        try {
            logJwt(jwt);

            final JSONObject userAccountDetails = getUserAccountDetails(jwt);

            JSONObject responseJson = new JSONObject();
            responseJson.put("id", userAccountDetails.getLong("id"));
            responseJson.put("roles", userAccountDetails.getJSONArray("roleBriefs"));

            return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
        } catch (Exception exception) {
            if (LOG.isInfoEnabled())
                LOG.info("Unexpected exception", exception);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * This method can only be called if a valid JWT token was provided
     * It returns the userId from the token
     */
    @GetMapping("/mysites")
    public ResponseEntity<String> getSites(@AuthenticationPrincipal Jwt jwt, @CurrentSecurityContext SecurityContext context) {
        try {
            logJwt(jwt);

            final JSONObject userAccountDetails = getUserAccountDetails(jwt);

            JSONObject responseJson = new JSONObject();
            responseJson.put("id", userAccountDetails.getLong("id"));
            responseJson.put("sites", userAccountDetails.getJSONArray("siteBriefs"));

            return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
        } catch (Exception exception) {
            if (LOG.isInfoEnabled())
                LOG.info("Unexpected exception", exception);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * This method can only be called if a valid JWT token was provided
     * It returns the userId from the token
     */
    @GetMapping("/myusergroups")
    public ResponseEntity<String> getUserGroups(@AuthenticationPrincipal Jwt jwt, @CurrentSecurityContext SecurityContext context) {
        try {
            logJwt(jwt);

            final JSONObject userAccountDetails = getUserAccountDetails(jwt);

            JSONObject responseJson = new JSONObject();
            responseJson.put("id", userAccountDetails.getLong("id"));
            responseJson.put("userGroups", userAccountDetails.getJSONArray("userGroupBriefs"));

            return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
        } catch (Exception exception) {
            if (LOG.isInfoEnabled())
                LOG.info("Unexpected exception", exception);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * This method can be called anonymously
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    /*
     * This method can only be called if a valid JWT token was provided
     * It returns the userId from the token
     */
    @GetMapping("/whoami")
    public ResponseEntity<String> getUser(@AuthenticationPrincipal Jwt jwt, @CurrentSecurityContext SecurityContext context) {
        try {
            logJwt(jwt);

            final JSONObject userAccountDetails = getUserAccountDetails(jwt);

            final JSONObject responseJson = new JSONObject();
            responseJson.put("id", userAccountDetails.getLong("id"));
            responseJson.put("name", userAccountDetails.getString("name"));

            return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
        } catch (Exception exception) {
            if (LOG.isInfoEnabled())
                LOG.info("Unexpected exception", exception);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}