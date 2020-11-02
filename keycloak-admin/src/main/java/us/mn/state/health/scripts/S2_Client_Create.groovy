package us.mn.state.health.scripts

import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.ClientRepresentation
import us.mn.state.health.configs.Constants
import us.mn.state.health.configs.KcEnvironment
import us.mn.state.health.utils.KeycloakConnection

import javax.ws.rs.core.Response


KcEnvironment environment = KcEnvironment.valueOf(System.console().readLine("Deployment Environment (TEST/PROD): ").toUpperCase())
Keycloak keycloak = KeycloakConnection.connect(environment)
createClient(keycloak, environment)
keycloak.close()

///////////////////////////////////////////////////////////////////////////////////////////////////
/// --------------- Details :) ----------------------------------------------------------------////
///////////////////////////////////////////////////////////////////////////////////////////////////
private static void createClient(Keycloak keycloak, KcEnvironment environment) {
    ClientRepresentation client = new ClientRepresentation()
    client.setClientId(Constants.appName)
    client.setName(client.getId())
    client.setBaseUrl(environment.appBaseUrl)
    client.setAdminUrl(environment.appAdminUrl)
    client.setRedirectUris(environment.appRedirectUri)
    client.serviceAccountsEnabled = true
    client.directAccessGrantsEnabled = true
    client.standardFlowEnabled = false
    client.publicClient = true
    ClientRepresentation clientRepresentation = client
    Response response = keycloak.realms().realm(Constants.realmId).clients().create(clientRepresentation)
    response.close()
}
