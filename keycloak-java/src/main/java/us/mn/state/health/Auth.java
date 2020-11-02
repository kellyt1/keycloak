package us.mn.state.health;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Auth {
    
    public Auth(){
    }
    
    public Map<String, Object> authenticate(String uname, String pw) throws IOException {
        String tokenendpoint ="";
        String userendpoint = "";
        String clientid = "";
        String server = "";
        String proxyUrl = "";
        int proxyPort = 0;
        InputStream is = getClass().getClassLoader().getResourceAsStream("../context.xml");
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            NodeList env = (NodeList) doc.getElementsByTagName("Environment");
            for(int i = 0 ; i < env.getLength(); i++){
                Element e = (Element)env.item(i);
                switch (e.getAttribute("name").toLowerCase()){
                    case "tokenendpoint":
                        tokenendpoint = e.getAttribute("value");
                        break;
                    case "userendpoint":
                        userendpoint = e.getAttribute("value");
                        break;
                    case "clientid":
                        clientid = e.getAttribute("value");
                        break;
                    case "server":
                        server = e.getAttribute("value");
                        break;
                    case "proxyurl":
                        proxyUrl = e.getAttribute("value");
                        break;
                    case "proxyport":
                        proxyPort = Integer.valueOf(e.getAttribute("value"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Map<String, Object> credentials = new HashMap<String, Object>();

        URLConnectionClientHandler ch = new URLConnectionClientHandler(new ConnectionFactory(proxyUrl, proxyPort));
        Client client = new Client(ch);
        String url = server + tokenendpoint;
        WebResource webResource = client.resource(url);
        MultivaluedMap queryParams = new MultivaluedMapImpl();

        queryParams.add("grant_type", "password");
        queryParams.add("client_id", clientid);
        queryParams.add("password", pw);
        queryParams.add("username", uname);

        String s = "";
        try{
            s = webResource.type("application/x-www-form-urlencoded").post(String.class, queryParams);
        } catch (UniformInterfaceException uif){
            System.out.print(uname + " with provided credentials was not authenticated:  "+ uif.getMessage());
            s = "Authentication Failed";
        } catch (Exception e) {
            System.out.println("Total Failure " + e.getMessage());
        }

        if (s.replaceAll("\\s+","").contains("\"access_token\"")) {
            if(isJson(s)){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Map<String, Object> map = mapper.readValue(s, new TypeReference<Map<String, String>>(){});
                    String userUrl = server + userendpoint;
                    WebResource wr =client.resource(userUrl);

                    String userInfoResponse = wr.type("application/x-www-form-urlencoded")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + map.get("access_token"))
                        .post(String.class);
                    Map<String, Object> userMap = mapper.readValue(userInfoResponse, new TypeReference<Map<String, String>>(){});
                    credentials.put("authorized", "true");
                    credentials.put("username",userMap.get("preferred_username"));
                    credentials.put("fName", userMap.get("given_name"));
                    credentials.put("lName", userMap.get("family_name"));
                    credentials.put("email", userMap.get("email"));
                } catch (IOException e) {
                    credentials.put("error", e);
                    credentials.put("authorized", "false");
                }
            }
        } else {
            credentials.put("error","NOT AUTHORIZED");
            credentials.put("authorized", "false");
        }
        
        return credentials;
    }
    
    private boolean isJson(String text){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(text);
            return true;
        } catch (IOException e) {
           return false;
        }
    }

    private class ConnectionFactory implements HttpURLConnectionFactory {
        private String proxyUrl;
        private int proxyPort;

        public ConnectionFactory(String proxyUrl, int proxyPort) {
            this.proxyUrl = proxyUrl;
            this.proxyPort = proxyPort;
        }

        @Override
        public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
            return (HttpURLConnection) url.openConnection(proxy);
        }
    }
    
}
