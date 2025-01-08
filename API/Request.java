package API;

// 3.84.50.79 - IP Address

/**
 * Contains static methods that make calls to the backend to get a response from
 * the api.
 */
public class Request {

    /**
     * The IP address to make the request to.
     */
    private static final String IP_ADDRESS = "http://34.236.100.216";

    /**
     * Makes a request to the backend using the IP address.
     */
    public static void makeRequest() {
        System.out.println("Making request to: " + IP_ADDRESS);
        // TODO add request logic.
    }
}
