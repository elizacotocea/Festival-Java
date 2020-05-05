import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientRpcWorker implements Runnable, IObserver {
    static final Logger logger = LogManager.getLogger(ClientRpcWorker.class);
    private IService server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public ClientRpcWorker(IService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
            logger.traceExit("PROXY SERVER: SUCCESSFUL ClientRpcWorker");
        } catch (IOException e) {
            e.printStackTrace();
            logger.traceExit("PROXY SERVER: FAILED ClientRpcWorker");
        }
    }


    public void run() { // aici intra din proxy, de la sendRequest()
        while(connected){
            try {
                Object request=input.readObject();
                    logger.traceEntry("--- citire: {}", request);
                    logger.trace("PROXY SERVER: run RECEIVED REQUEST");
                    Response response=handleRequest((Request)request); // next->handle request
                    if (response!=null){
                        sendResponse(response);
                        logger.trace("PROXY SERVER: run SENT RESPONSE");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            logger.traceExit("PROXY SERVER: run : BEGIN SHUTDOWN");
            input.close();
            output.close();
            connection.close();
            logger.traceExit("PROXY SERVER: run : COMPLETED SHUTDOWN");
        } catch (IOException e) {
            logger.traceExit("PROXY SERVER: run : IOException");
            System.out.println("Error "+e);
        }
    }


    @Override
    public void notifyTicketsSold(Show show) throws ServiceException {
        ShowDTO showDTO = DTOUtils.getDTO(show);
        Response resp = new Response.Builder().type(ResponseType.UPDATED_SHOWS).data(showDTO).build();
        logger.trace("PROXY SERVER: ticketsSold BUILT RESPONSE");
        System.out.println("Tickets sold for show " + show);
        try {
            sendResponse(resp);
            logger.traceExit("PROXY SERVER: ticketsSold SENT RESPONSE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();


    private Response handleRequest(Request request){
        Response response=null;
        if (request.type()== RequestType.LOGIN){
            logger.trace("PROXY SERVER: handleRequest RECEIVED REQUEST type==RequestType.LOGIN");
            System.out.println("Login request ..."+request.type());
            UserDTO udto=(UserDTO)request.data();
            Employee user= DTOUtils.getFromDTO(udto);
            try {
                server.login(user, this);
                logger.trace("PROXY SERVER: handleRequest SENT COMMAND TO SERVER server.login");
                return okResponse;
            } catch (ServiceException e) {
                connected=false;
                logger.traceExit("PROXY SERVER: FAILED handleRequest type==RequestType.LOGIN");
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.LOGOUT){
            logger.trace("PROXY SERVER: handleRequest RECEIVED REQUEST type==RequestType.LOGOUT");
            System.out.println("Logout request");
            UserDTO udto=(UserDTO)request.data();
            Employee user= DTOUtils.getFromDTO(udto);
            try {
                server.logout(user, this);
                connected=false;
                logger.trace("PROXY SERVER: handleRequest SENT COMMAND TO SERVER server.logout");
                return okResponse;

            } catch (ServiceException e) {
                logger.traceExit("PROXY SERVER: FAILED handleRequest type==RequestType.LOGOUT");
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type()== RequestType.GET_SHOWS){
            logger.trace("PROXY SERVER: handleRequest RECEIVED REQUEST type==RequestType.GET_MATCHES");
            System.out.println("Get Matches request");
            try {
                Show[] shows = server.findAllShows();
                ShowDTO[] mecidtos = DTOUtils.getDTO(shows);
                logger.trace("PROXY SERVER: handleRequest SENT COMMAND TO SERVER server.findAllMeci");
                return new Response.Builder().type(ResponseType.GET_SHOWS).data(mecidtos).build();

            } catch (ServiceException e) {
                logger.traceExit("PROXY SERVER: FAILED handleRequest type==RequestType.GET_MATCHES");
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }


        if (request.type()== RequestType.TICKETS_SOLD) {
            logger.trace("PROXY SERVER: handleRequest RECEIVED REQUEST type==RequestType.TICKETS_SOLD");
            Object[] data = (Object[])request.data();
            ShowDTO showDTO = (ShowDTO)data[0];
            TicketDTO tcket = (TicketDTO)data[1];
            Show s = DTOUtils.getFromDTO(showDTO);
            Ticket ticket = DTOUtils.getFromDTO(tcket);
            try
            {
                Show shU=server.ticketsSold(s, ticket); // this is the response of the TICKETS_SOLD request
                logger.trace("PROXY SERVER: handleRequest SENT COMMAND TO SERVER server.ticketsSold");
                ShowDTO sDTO = DTOUtils.getDTO(shU);

                return okResponse; // de aici vine double update-ul bun (aici trimiteai bine)
                //aici trimiti doar o confirmare, nu un update. AICI era. pentru ca el astepta in continuare ok-ul, dar notificarea ta era prinsa in update . asa zic

            }
            catch (ServiceException e)
            {
                logger.trace("PROXY SERVER: FAILED handleRequest type==RequestType.TICKETS_SOLD");
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        logger.trace("PROXY SERVER: RETURN RESPONSE");
        return response;
    }


    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        output.writeObject(response);
        logger.traceEntry("--- scriere: {}",response);
        output.flush();
        logger.traceExit("PROXY SERVER: SUCCESSFUL sendResponse");
    }
}

