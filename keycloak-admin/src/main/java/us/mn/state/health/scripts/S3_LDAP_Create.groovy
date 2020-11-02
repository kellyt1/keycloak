package us.mn.state.health.scripts

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.keycloak.representations.idm.ComponentRepresentation
import us.mn.state.health.configs.Constants
import us.mn.state.health.configs.KcEnvironment
import us.mn.state.health.configs.LdapEnvironment
import us.mn.state.health.utils.KeycloakConnection
import us.mn.state.health.utils.UserFederationProviderFactory

import javax.ws.rs.client.Entity
import javax.ws.rs.core.Form
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder


KcEnvironment kcEnvironment = KcEnvironment.valueOf(System.console().readLine("Deployment Environment (TEST/PROD): ").toUpperCase())
createUserFederationProvider(kcEnvironment, LdapEnvironment.MDHAD)

///////////////////////////////////////////////////////////////////////////////////////////////////
/// --------------- Details :) ----------------------------------------------------------------////
///////////////////////////////////////////////////////////////////////////////////////////////////
private static void createUserFederationProvider(KcEnvironment kcEnvironment, LdapEnvironment ldapEnvironment) {

    Form tokenForm = new Form()
        .param("grant_type", "password")
        .param("client_id", Constants.adminClientId)
        .param("username", KeycloakConnection.adminUserPrompt())
        .param("password", KeycloakConnection.adminPasswordPrompt())
    Entity<Form> tokenEntity = Entity.form(tokenForm)

    ComponentRepresentation ldapUserFederationProvider = UserFederationProviderFactory.createUserFederationProvider(ldapEnvironment)
    ObjectMapper mapper = new ObjectMapper()
    String ldapConfigJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ldapUserFederationProvider)

    String tokenResponse = new ResteasyClientBuilder()
        .build()
        .target(UriBuilder.fromPath(kcEnvironment.tokenEndpoint))
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .post(tokenEntity, String.class)

    Map<String, Object> map = mapper.readValue(tokenResponse, new TypeReference<Map<String, String>>(){});
    String bearerToken = map.get("access_token");

    Response createLdapResponse = new  ResteasyClientBuilder()
        .build()
        .target(UriBuilder.fromPath(kcEnvironment.adminComponentEndpoint))
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + bearerToken)
        .post(Entity.json(ldapConfigJson))

    createLdapResponse
}
