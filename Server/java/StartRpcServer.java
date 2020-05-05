import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {

        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-server.xml");
//        AbstractServer server = new RpcConcurrentServer(55555, (IService)factory.getBean("appService"));
//        try {
//            server.start();
//        } catch (ServerException e) {
//            System.err.println("Error starting the server" + e.getMessage());
//        } finally {
//            try {
//                server.stop();
//            } catch (ServerException e) {
//                System.err.println("Error stopping server " + e.getMessage());
//            }
//        }
//        Properties serverProps = new Properties();
//        try {
//            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
//            System.out.println("Server properties set. ");
//            serverProps.list(System.out);
//        } catch (IOException e) {
//            System.err.println("Cannot find server.properties " + e);
//            return;
//        }
//
//        Properties properties = new Properties();
//        try {
//            properties.load(JdbcUtils.class.getResourceAsStream("/bd.config"));
//            properties.list(System.out);
//        } catch (IOException e) {
//            System.err.println("Cannot find bd.config " + e);
//            return;
//        }
//        JdbcUtils jdbcInv = new JdbcUtils(properties);
//
//
//        EmployeeRepository userRepo = new EmployeeRepository(properties);
//        ShowRepository showRepo = new ShowRepository(properties);
//        TicketRepository ticketRepo=new TicketRepository(properties);
//
//        IService chatServerImpl = new ServerImpl(userRepo, showRepo,ticketRepo);
//        int serverPort = defaultPort;
//        try {
//            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
//        } catch (NumberFormatException nef) {
//            System.err.println("Wrong  Port Number" + nef.getMessage());
//            System.err.println("Using default port " + defaultPort);
//        }
//        System.out.println("Starting server on port: " + serverPort);
//        AbstractServer server = new RpcConcurrentServer(serverPort, chatServerImpl);
//        try {
//            server.start();
//        } catch (ServerException e) {
//            System.err.println("Error starting the server" + e.getMessage());
//        } finally {
//            try {
//                server.stop();
//            } catch (ServerException e) {
//                System.err.println("Error stopping server " + e.getMessage());
//            }
//        }
    }
}

