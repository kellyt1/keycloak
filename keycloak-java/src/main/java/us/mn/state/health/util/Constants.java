package us.mn.state.health.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URL;

/**
 * Created by malmsh1 on 3/19/2018.
 */
public class Constants {
    public static String REALM = "admin-apps-realm";
    public static String TOKEN_ENDPOINT ="/realms/admin-apps-realm/protocol/openid-connect/token";
    public static String USER_ENDPOINT = "/realms/admin-apps-realm/protocol/openid-connect/userinfo";
    public static String CLIENT_ID = "hrm-portal";
    

}
