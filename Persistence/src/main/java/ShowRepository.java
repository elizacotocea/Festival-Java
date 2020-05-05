import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ShowRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(ShowRepository.class);

    private int maxID=0;
    private int getMaxID(){
        List<ShowDTO> all=findAll();
        for (ShowDTO b:all){
            if (b.getId()>maxID)
                maxID=b.getId();
        }
        return maxID+1;
    }
    public ShowRepository(Properties props ) {
        dbUtils=new JdbcUtils(props);
        maxID=getMaxID();
    }

    public int save(ShowDTO ex){
        logger.traceEntry("saving show with id {}",ex.getId());
        Connection con=dbUtils.getConnection();
        try {
            PreparedStatement preparedStatement=con.prepareStatement("insert into show values (?,?,?,?,?,?,?)");
            preparedStatement.setInt(1,ex.getId());
            preparedStatement.setString(2,ex.getDataTimp().toString());
            preparedStatement.setString(3,ex.getLocation());
            preparedStatement.setInt(4,ex.getNrAvailableSeats());
            preparedStatement.setInt(5,ex.getNrSoldSeats());
            preparedStatement.setString(6,ex.getArtistName());
            int result=preparedStatement.executeUpdate();
            return result;
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logger.traceExit();
        return -1;
    }

    public int delete(Integer ID) {
        logger.traceEntry("deleting show with {}",ID);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from show where ID=?")){
            preStmt.setInt(1,ID);
            int result=preStmt.executeUpdate();
            return result;
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);

        }
        finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logger.traceExit();
        return -1;

    }

    public int update(Integer ID, ShowDTO ex) {
        logger.traceEntry("Update a person with id {}",ID);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preparedStatement=con.prepareStatement("update show set ID=?," +
                "location=?,nrAvailableSeats=?,nrSoldSeats=?,artistName=? where ID=?")){
            preparedStatement.setInt(1,ex.getId());
            preparedStatement.setString(2,ex.getLocation());
            preparedStatement.setInt(3,ex.getNrAvailableSeats());
            preparedStatement.setInt(4,ex.getNrSoldSeats());
            preparedStatement.setString(5,ex.getArtistName());
            preparedStatement.setInt(6,ID);
            int result=preparedStatement.executeUpdate();
            return result;
        }catch (SQLException exc){
            logger.error(exc);
            System.out.println("Error DB "+exc);
        }
        finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public ShowDTO findOne(int ID){
        logger.traceEntry("finding show with id {}",ID);
        Connection con=dbUtils.getConnection();
        ShowDTO sh=null;
        try{
            PreparedStatement preStmt=con.prepareStatement("select * from show where ID=?");
            preStmt.setInt(1,ID);
            ResultSet rs=preStmt.executeQuery();
            while (rs.next()){
                String location= rs.getString("location");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime data= LocalDateTime.parse(rs.getString("showDateTime"),formatter);
                int av=rs.getInt("nrAvailableSeats");
                int sold=rs.getInt("nrSoldSeats");
                String a=rs.getString("artistName");
                sh = new ShowDTO(ID,data.toString(),location,av,sold,a);
                logger.traceExit();
                return sh;
            }

        } catch (SQLException e) {
            logger.error(sh);
            System.out.println("Error DB"+sh);
        }
        finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logger.traceExit("No show found with this id {}",ID);
        return null;
    }


    public List<ShowDTO> findAll() {
        Connection con=dbUtils.getConnection();
        List<ShowDTO> shows=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from show")) {
            try(ResultSet rs=preStmt.executeQuery()) {
                while (rs.next()) {
                    int ID = rs.getInt("ID");
                    String location= rs.getString("location");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime data= LocalDateTime.parse(rs.getString("showDateTime").replace("T"," "),formatter);
                    int av=rs.getInt("nrAvailableSeats");
                    int sold=rs.getInt("nrSoldSeats");
                    String a=rs.getString("artistName");
                    ShowDTO sh = new ShowDTO(ID,data.toString(),location,av,sold,a);
                    shows.add(sh);
                }}}
        catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logger.traceExit(shows);
        return shows;
    }
}

