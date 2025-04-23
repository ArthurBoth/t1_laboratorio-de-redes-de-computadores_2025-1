import java.net.SocketException;

public class App {
    public static void main(String[] args) throws SocketException {
        new network.NetworkManager().start();
    }
}
