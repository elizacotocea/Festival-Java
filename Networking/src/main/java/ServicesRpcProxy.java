import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRpcProxy implements IService {
    static final Logger logger = LogManager.getLogger(ServicesRpcProxy.class);
    private String host;
    private int port;
    private Boolean isInitialized = false;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
        logger.trace("CLIENT SIDE PROXY INIT");
    }

    @Override
    public void login(Employee user, IObserver client) throws ServiceException {
        if (!isInitialized) {
            initializeConnection();
            this.isInitialized = true;
        }
        UserDTO udto = DTOUtils.getDTO(user);
        Request req = new Request.Builder().type(RequestType.LOGIN).data(udto).build();
        sendRequest(req);
        logger.trace("PROXY CLIENT: login SENT REQUES");
        Response response = readResponse();
        logger.trace("PROXY CLIENT: login RECEIVED RESPONSE");
        if (response.type() == ResponseType.OK) {
            this.client = client;
            logger.traceExit("PROXY CLIENT: SUCCESSFUL login");
            return;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            logger.traceExit("PROXY CLIENT: FAILED login");
            throw new ServiceException(err);
        }
    }

    @Override
    public void logout(Employee user, IObserver client) throws ServiceException {
        UserDTO udto = DTOUtils.getDTO(user);
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(udto).build();
        sendRequest(req);
        logger.trace("PROXY CLIENT: logout SENT REQUEST");
        Response response = readResponse();
        logger.trace("PROXY CLIENT: logout RECEIVED RESPONSE");
        closeConnection();
        this.isInitialized = false;
        logger.traceExit("PROXY CLIENT: SUCCESSFUL logout");
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            logger.traceExit("PROXY CLIENT: FAILED logout");
            throw new ServiceException(err);
        }
    }

    @Override
    public Show[] findAllShows() throws ServiceException {
        if (!isInitialized) {
            initializeConnection();
            this.isInitialized = true;
        }
        Request req = new Request.Builder().type(RequestType.GET_SHOWS).build();
        sendRequest(req);
        logger.trace("PROXY CLIENT: findAllMeci SENT REQUEST");
        Response response = readResponse();
        logger.trace("PROXY CLIENT: findAllMeci RECEIVED RESPONSE " );
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            logger.traceExit("PROXY CLIENT: FAILED findAllMeci" );
            throw new ServiceException(err);
        }
        ShowDTO[] shDTOS = (ShowDTO[]) response.data();
        Show[] shs = DTOUtils.getFromDTO(shDTOS);
        logger.traceExit("PROXY CLIENT: SUCCESSFUL findAllMeci");
        return shs;
    }


    @Override
    public Show ticketsSold(Show show, Ticket t) throws ServiceException {
        ShowDTO meciDTO = DTOUtils.getDTO(show);
        TicketDTO tDTO = DTOUtils.getDTO(t);
        Object[] sendData = new Object[2];
        sendData[0] = meciDTO;
        sendData[1] = tDTO;
        Request req = new Request.Builder().type(RequestType.TICKETS_SOLD).data(sendData).build();
        sendRequest(req);
        logger.trace("PROXY CLIENT: ticketsSold SENT REQUEST ");
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
        logger.traceExit("PROXY CLIENT: SUCCESSFUL ticketsSold");
        return null;
    }


    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
            logger.traceExit("PROXY CLIENT: SUCCESSFUL closeConnection");
        } catch (IOException e) {
            logger.traceExit("PROXY CLIENT: FAILED closeConnection");
            e.printStackTrace();
        }

    }


    private void sendRequest(Request request) throws ServiceException {

        if (!isInitialized) {
            initializeConnection();
            this.isInitialized = true;
        }

        logger.traceEntry("NETWORKING FROM CLIENT PROXY TO SERVER: INITIALIZING sendRequest");
        try {
            output.writeObject(request);
            logger.traceEntry("--- scriere: {}", request);
            output.flush();
            logger.traceExit("NETWORKING FROM CLIENT PROXY TO SERVER: SUCESSFUL sendRequest");
        } catch (IOException e) {
            logger.traceExit("NETWORKING FROM CLIENT PROXY TO SERVER: FAILED sendRequest");
            throw new ServiceException("Error sending object " + e);
        }

    }

    private Response readResponse() throws ServiceException {
        Response response = null;
        logger.traceEntry("NETWORKING FROM CLIENT PROXY TO SERVER: INITIALIZING readResponse");
        try {
            response = qresponses.take();
            logger.traceEntry("--- citire: {}", response);
            logger.traceExit("NETWORKING FROM CLIENT PROXY TO SERVER: SUCESSFUL qresponses.take readResponse");
        } catch (InterruptedException e) {
            logger.traceExit("NETWORKING FROM CLIENT PROXY TO SERVER: FAILED readResponse");
            e.printStackTrace();
        }
        return response;
    }


    private void initializeConnection() throws ServiceException {
        if (!this.isInitialized) {
            try {
                logger.traceEntry("PROXY CLIENT: INITIALIZING initializeConnection");
                connection = new Socket(host, port);
                output = new ObjectOutputStream(connection.getOutputStream());
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());
                finished = false;
                this.isInitialized = true;
                startReader();
                logger.traceExit("PROXY CLIENT: SUCCESSFUL initializeConnection");
            } catch (IOException e) {
                logger.traceExit("PROXY CLIENT: FAILED initializeConnection");
                e.printStackTrace();
            }
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response) {

        if (response.type() == ResponseType.UPDATED_SHOWS) {
            Show sh = DTOUtils.getFromDTO((ShowDTO) response.data());
            try {
                client.notifyTicketsSold(sh);
            } catch (ServiceException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.UPDATED_SHOWS;
    }


    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    logger.traceEntry("PROXY CLIENT: READING DATA FROM THE SERVER");
                    System.out.println("response received " + response);
                    if (isUpdate((Response) response)) {
                        logger.traceEntry("PROXY CLIENT: TRACE isUpdate((Response)response) == true");
                        handleUpdate((Response) response);
                    } else {
                        try {
                            logger.traceEntry("PROXY CLIENT: TRACE qresponses.put((Response)response)");
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    logger.traceExit("PROXY CLIENT: FAILED run (because other end of socket disconnected from server)");
                    System.out.println("Reading error " + e);
                } catch (ClassNotFoundException e) {
                    logger.traceExit("PROXY CLIENT: FAILED run : ClassNotFoundException");
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
