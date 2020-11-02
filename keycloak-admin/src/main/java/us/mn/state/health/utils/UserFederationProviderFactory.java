package us.mn.state.health.utils;

import org.keycloak.representations.idm.ComponentRepresentation;
import us.mn.state.health.configs.Constants;
import us.mn.state.health.configs.LdapEnvironment;

public class UserFederationProviderFactory {

    public static ComponentRepresentation createUserFederationProvider(LdapEnvironment ldapEnvironment){
        ComponentRepresentation userFederationProvider = new ComponentRepresentation();

        userFederationProvider.setName(ldapEnvironment.getName());
        userFederationProvider.setProviderId(ldapEnvironment.getProviderId());
        userFederationProvider.setProviderType(ldapEnvironment.getProviderType());
        userFederationProvider.setParentId(Constants.realmId);
        userFederationProvider.setConfig(ldapEnvironment.getLdapConfig());

        return userFederationProvider;
    }
}
