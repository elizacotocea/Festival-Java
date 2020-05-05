import java.io.Serializable;
import java.time.LocalDateTime;

public class Show implements Serializable {
    private int id;
    private LocalDateTime dataTimp;
    private String location;
    private int nrAvailableSeats;
    private int nrSoldSeats;
    private String artistName;

    public Show(int ID, LocalDateTime d,String location, int nrAvailableSeats, int nrSoldSeats, String Artist) {
        this.id=ID;
        this.dataTimp=d;
        this.location = location;
        this.nrAvailableSeats = nrAvailableSeats;
        this.nrSoldSeats = nrSoldSeats;
        this.artistName = Artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LocalDateTime getDataTimp() {
        return dataTimp;
    }

    public void setDataTimp(LocalDateTime dataTimp) {
        this.dataTimp = dataTimp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getNrAvailableSeats() {
        return nrAvailableSeats;
    }

    public void setNrAvailableSeats(int nrAvailableSeats) {
        this.nrAvailableSeats = nrAvailableSeats;
    }

    public int getNrSoldSeats() {
        return nrSoldSeats;
    }

    public void setNrSoldSeats(int nrSoldSeats) {
        this.nrSoldSeats = nrSoldSeats;
    }

    public int getID() {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public String toString() {
        return super.toString()+";"
                +dataTimp +
                ";" + location  +
                ";" + nrAvailableSeats +
                ";" + nrSoldSeats +
                ";" + artistName
               ;
    }
}
