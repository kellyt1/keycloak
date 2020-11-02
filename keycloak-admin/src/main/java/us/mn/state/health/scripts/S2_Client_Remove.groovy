package us.mn.state.health.scripts

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import us.mn.state.health.configs.Constants
import us.mn.state.health.configs.KcEnvironment
import us.mn.state.health.utils.ApiUtil
import us.mn.state.health.utils.KeycloakConnection


Keycloak keycloak = KeycloakConnection.connect(KcEnvironment.valueOf(System.console().readLine("Deployment Environment (TEST/PROD): ").toUpperCase()))

RealmResource realm = keycloak.realms().realm(Constants.realmId)
ApiUtil.findClientByClientId(realm,Constants.appName).remove()
keycloak.close()
