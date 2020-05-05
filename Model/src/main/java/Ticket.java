import java.io.Serializable;

public class Ticket implements Serializable {
    private int id;
    private int nrWantedSeats;
    private String buyerName;
    private int idShow;

    public Ticket( int ID, int nrWantedSeats, String bName, int idShow) {
        this.id=ID;
        this.nrWantedSeats = nrWantedSeats;
        this.buyerName=bName;
        this.idShow = idShow;
    }

    public int getNrWantedSeats() {
        return nrWantedSeats;
    }

    public void setNrWantedSeats(int nrWantedSeats) {
        this.nrWantedSeats = nrWantedSeats;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyeName) {
        this.buyerName = buyeName;
    }

    public int getIdShow() {
        return idShow;
    }

    public void setIdShow(int idShow) {
        this.idShow = idShow;
    }

    public int getID() {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
}
