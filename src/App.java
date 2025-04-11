import java.net.UnknownHostException;

public class App {
    public static void main(String[] args) throws UnknownHostException {
        new network.NetworkManager().start();
    }
}
