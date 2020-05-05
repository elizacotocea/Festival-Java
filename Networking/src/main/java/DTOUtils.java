public class DTOUtils {
    public static Employee getFromDTO(UserDTO usdto){
        int id=usdto.getId();
        String pass=usdto.getPassword();
        String username=usdto.getUsername();
        Employee client = new Employee(id, username,pass);
        return client;
    }
    public static UserDTO getDTO(Employee user){
        int id=user.getID();
        String pass=user.getPassword();
        String nume=user.getUsername();
        return new UserDTO(id, pass, nume);
    }

    public static UserDTO[] getDTO(Employee[] users){
        UserDTO[] frDTO=new UserDTO[users.length];
        for(int i=0;i<users.length;i++)
            frDTO[i]=getDTO(users[i]);
        return frDTO;
    }

    public static Employee[] getFromDTO(UserDTO[] users){
        Employee[] friends=new Employee[users.length];
        for(int i=0;i<users.length;i++){
            friends[i]=getFromDTO(users[i]);
        }
        return friends;
    }


    public static Show getFromDTO(ShowDTO showDTO) {
        return new Show(showDTO.getId(),showDTO.getDataTimp(),showDTO.getLocation(),showDTO.getNrAvailableSeats(),
                showDTO.getNrSoldSeats(),showDTO.getArtistName());
    }
    public static Show[] getFromDTO(ShowDTO[] showDTOs) {
        Show[] showuri = new Show[showDTOs.length];
        for (int i = 0; i < showDTOs.length; i++) {
            showuri[i] = getFromDTO(showDTOs[i]);
        }
        return showuri;
    }
    public static ShowDTO getDTO(Show show){
        return new ShowDTO(show.getID(),show.getDataTimp(),show.getLocation(),show.getNrAvailableSeats(),
                show.getNrSoldSeats(),show.getArtistName());
    }

    public static ShowDTO[] getDTO(Show[] showuri){
        ShowDTO[] showuriDTO = new ShowDTO[showuri.length];
        for (int i = 0; i < showuri.length; i++) {
            showuriDTO[i] = getDTO(showuri[i]);
        }
        return showuriDTO;
    }



    public static Ticket[] getFromDTO(TicketDTO[] bileteDTOs) {
        Ticket[] bilete = new Ticket[bileteDTOs.length];
        for (int i = 0; i < bileteDTOs.length; i++) {
            bilete[i] = getFromDTO(bileteDTOs[i]);
        }
        return bilete;
    }
    public static TicketDTO[] getDTO(Ticket[] bilete){
        TicketDTO[] biletdtos = new TicketDTO[bilete.length];
        for (int i = 0; i < bilete.length; i++) {
            biletdtos[i] = getDTO(bilete[i]);
        }
        return biletdtos;
    }
    public static TicketDTO getDTO(Ticket bilet){
        return new TicketDTO(bilet.getID(),bilet.getNrWantedSeats(),bilet.getBuyerName(),bilet.getIdShow());
    }
    public static Ticket getFromDTO(TicketDTO biletDTO){
        return new Ticket(biletDTO.getId(),biletDTO.getNrWantedSeats(),biletDTO.getBuyerName(),biletDTO.getIdShow());
    }
}

