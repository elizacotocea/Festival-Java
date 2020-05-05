import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class AppServer {
    public static AppHandler handler;
    public static AppService.Processor processor;

    public static void main(String[] args) {
        try {
            handler = new AppHandler();
            processor = new AppService.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(AppService.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9091);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            System.out.println("Hiii, I'm waiting...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
