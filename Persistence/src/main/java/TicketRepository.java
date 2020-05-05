import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TicketRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(TicketRepository.class);

    private int maxID=0;
    private int getMaxID(){
        List<Ticket> all=findAll();
        for (Ticket b:all){
            if (b.getID()>maxID)
                maxID=b.getID();
        }
        return maxID+1;
    }
    public TicketRepository(Properties props) {
        dbUtils=new JdbcUtils(props);
        maxID=getMaxID();
    }

    public int save(TicketDTO ex){
        logger.traceEntry("saving ticket with id {}",ex.getId());
        Connection con=dbUtils.getConnection();
        try {
            PreparedStatement preparedStatement=con.prepareStatement("insert into ticket values (?,?,?,?)");
            preparedStatement.setInt(1,ex.getId());
            preparedStatement.setInt(2,ex.getNrWantedSeats());
            preparedStatement.setString(3,ex.getBuyerName());
            preparedStatement.setInt(4,ex.getIdShow());
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
        logger.traceEntry("deleting ticket with {}",ID);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from ticket where ID=?")){
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

    public int update(Integer ID, Ticket entity) {
        logger.traceEntry("Update a person with id {}",ID);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("update ticket set ID=?,nrSeatsWanted=?," +
                "buyerName=?,idShow=? where ID=?")){
            preStmt.setInt(1,entity.getID());
            preStmt.setInt(2,entity.getNrWantedSeats());
            preStmt.setString(3,entity.getBuyerName());
            preStmt.setInt(4,entity.getIdShow());
            preStmt.setInt(5,ID);
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
        return -1;
    }

    public Ticket findOne(int ID){
        logger.traceEntry("finding ticket with id {}",ID);
        Connection con=dbUtils.getConnection();
        Ticket ex=null;
        try{
            PreparedStatement preStmt=con.prepareStatement("select * from ticket where ID=?");
            preStmt.setInt(1,ID);
            ResultSet rs=preStmt.executeQuery();
            while (rs.next()){
                int wanted = rs.getInt("nrSeatsWanted");
                String buyerName=rs.getString("buyerName");
                int idShow=rs.getInt("idShow");
                ex = new Ticket(ID, wanted,buyerName,idShow);
                logger.traceExit();
                return ex;
            }

        } catch (SQLException e) {
            logger.error(ex);
            System.out.println("Error DB"+ex);
        }
        finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logger.traceExit("No ticket found with this id {}",ID);
        return null;
    }


    public List<Ticket> findAll() {
        Connection con=dbUtils.getConnection();
        List<Ticket> tickets=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from ticket")) {
            try(ResultSet rs=preStmt.executeQuery()) {
                while (rs.next()) {
                    int ID=rs.getInt("ID");
                    int wanted = rs.getInt("nrSeatsWanted");
                    String buyerName=rs.getString("buyerName");
                    int idShow=rs.getInt("idShow");
                    Ticket ex = new Ticket(ID, wanted,buyerName,idShow);
                    tickets.add(ex);
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
        logger.traceExit(tickets);
        return tickets;
    }
}

