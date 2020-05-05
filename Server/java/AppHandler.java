import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppHandler implements AppService.Iface {

    private Properties serverProps;
    private EmployeeRepository userRepository;
    private ShowRepository showRepository;
    private TicketRepository ticketRepository;
    private ServiceImpl ServerImpl;
    private ArrayList<Client> loggedClients = new ArrayList<>();
    private ArrayList<String> loggedClientsUsernames = new ArrayList<>();

    public AppHandler() {
        serverProps = new Properties();
        try {
            serverProps.load(AppHandler.class.getResourceAsStream("bd.config"));
        } catch (
                IOException e) {
            System.err.println("Cannot find bd.config " + e);
            return;
        }
        userRepository=new EmployeeRepository(serverProps);
        showRepository=new ShowRepository(serverProps);
        ticketRepository=new TicketRepository(serverProps);
        ServerImpl = new ServiceImpl(userRepository,showRepository,ticketRepository);
    }

    private void alertClients() {
        for (Client loggedClient : loggedClients) {
            try {
                TTransport transport = new TSocket("localhost", loggedClient.getPort());
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                NotifyService.Client client = new NotifyService.Client(protocol);
                client.notify("update");
                transport.close();
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String login(String username, String password, String host, int port) throws TException {
        if (!loggedClientsUsernames.contains(username)) {
            if (ServerImpl.login(username, password)) {
                loggedClients.add(new Client(host, port));
                loggedClientsUsernames.add(username);
                return "loggedIn";
            } else {
                return "loggedFailed";
            }
        }
        else{
            return "alreadyLoggedIn";
        }

    }

    @Override
    public List<ShowDTO> findAllShows() throws TException {
        return ServerImpl.findAllShows();
    }

    @Override
    public void ticketsSold(ShowDTO showDTO, TicketDTO ticketDTO) throws TException {
        ServerImpl.ticketsSold(showDTO,ticketDTO);
        alertClients();
    }

    @Override
    public String logOut(String username,String host, int port) throws TException {
        loggedClients.remove(new Client(host, port));
        loggedClientsUsernames.remove(username);
        return "loggedOut";
    }


}

