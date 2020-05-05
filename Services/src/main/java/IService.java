import java.util.List;

public interface IService {
    boolean login(String username, String password);
    List<ShowDTO> findAllShows();
    ShowDTO ticketsSold(ShowDTO show, TicketDTO ticket);
    boolean logout(String username);
}
