import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
    void notifyTicketsSold(Show show) throws ServiceException, RemoteException;

}
