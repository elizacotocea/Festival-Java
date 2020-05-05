import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceImpl implements IService {

    static final Logger logger = LogManager.getLogger(ServiceImpl.class);
    private EmployeeRepository userRepository;
    private ShowRepository showRepository;
    private TicketRepository ticketRepository;
    private Map<String, IObserver> loggedClients;

    public ServiceImpl(EmployeeRepository uRepo, ShowRepository sRepo, TicketRepository tRepo) {
        userRepository = uRepo;
        showRepository=sRepo;
        ticketRepository=tRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    private static final int defaultThreadsNo = 5;

//    private void notifyTicketsBought(ShowDTO show)  {
//        List<Employee> users = userRepository.findAll();
//        logger.trace("FULL SERVER: notify tickets bought!");
//        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
//
//        for(Employee us :users) {
//            IObserver chatClient = loggedClients.get(us.getUsername());
//            if (chatClient != null)
//                executor.execute(() -> {
//                    try {
//                        logger.trace("I AM IN NOTIFY TICKETS BOUGHT");
//                        System.out.println("Notifying [" + us.getUsername() + "] tickets were bought for show [" + show.getId() + "].");
//                        //chatClient.notifyTicketsSold(show);
//                    } catch (Exception e) {
//                        System.out.println("Error notifying friend " + e);
//                    }
//                });
//        }
//        executor.shutdown();
//    }


    public synchronized boolean login(String username,String passwd) {
        return (userRepository.FindByUsernameAndPassword(username,passwd)!=null);
    }



    public synchronized boolean logout(String username) {
        IObserver localClient=loggedClients.remove(username);
        return (localClient!=null);
    }

    @Override
    public synchronized List<ShowDTO> findAllShows(){
        List<ShowDTO> shows= StreamSupport.stream(showRepository.findAll().spliterator(), false).collect(Collectors.toList());
        return shows;
    }


    @Override
    public synchronized ShowDTO ticketsSold(ShowDTO show, TicketDTO ticket) {
        ShowDTO sh = showRepository.findOne(show.getId());
        ShowDTO shUpd=new ShowDTO(sh.getId(),sh.getDataTimp(),sh.getLocation(),sh.getNrAvailableSeats()-
                ticket.getNrWantedSeats(),sh.getNrSoldSeats()+ticket.getNrWantedSeats(),sh.getArtistName());
        showRepository.update(sh.getId(),shUpd);
        ticketRepository.save(ticket);
//        Runnable runnable = () -> {// hopefully not a deadlock (has enough time to finish)
//            try {
//                Thread.sleep(1000);
//                notifyTicketsBought(shUpd);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
//        logger.trace("FULL SERVER: ticketsSold SENT OBSERVER COMMAND TO notifyTicketsBought");
        return shUpd;
    }

}



