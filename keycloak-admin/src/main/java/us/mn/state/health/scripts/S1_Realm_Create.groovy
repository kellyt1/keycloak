package us.mn.state.health.scripts

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmsResource
import us.mn.state.health.configs.Constants

import us.mn.state.health.configs.KcEnvironment
import us.mn.state.health.utils.KeycloakConnection
import us.mn.state.health.utils.PasswordPolicy
import us.mn.state.health.utils.RealmFactory

KcEnvironment environment = KcEnvironment.valueOf(System.console().readLine("Deployment Environment (TEST): ").toUpperCase())
Keycloak keycloak = KeycloakConnection.connect(environment)
RealmsResource realms = keycloak.realms()
realms.create(RealmFactory.createRealm(Constants.realmId,Constants.realmDisplayName,environment.sslRequired,PasswordPolicy.valueOf(System.console().readLine("Password Policy (LOCAL): ").toUpperCase())))
keycloak.close()
