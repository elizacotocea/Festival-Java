import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(EmployeeRepository.class);

    private int maxID=0;
    private int getMaxID(){
        List<Employee> all=findAll();
        for (Employee b:all){
            if (b.getID()>maxID)
                maxID=b.getID();
        }
        return maxID+1;
    }
    public EmployeeRepository(Properties props) {
        dbUtils=new JdbcUtils(props);
        maxID=getMaxID();
    }

    public Employee FindByUsernameAndPassword(String user,String passwd){
        logger.traceEntry("finding employee with username {}",user);
        Connection con=dbUtils.getConnection();
        Employee ex=null;
        try{
            PreparedStatement preStmt=con.prepareStatement("select * from employee where username=? and password=?");
            preStmt.setString(1,user);
            preStmt.setString(2,passwd);
            ResultSet rs=preStmt.executeQuery();
            while (rs.next()){
                int ID=rs.getInt("ID");
                String name = rs.getString("username");
                String passwdd=rs.getString("password");
                ex = new Employee(ID,name,passwdd);
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
        logger.traceExit("No employee found with this username {}",user);
        return null;
    }

    public int save(Employee ex){
        logger.traceEntry("saving employee with id {}",ex.getID());
        Connection con=dbUtils.getConnection();
        try {
            PreparedStatement preparedStatement=con.prepareStatement("insert into employee values (?,?,?)");
            preparedStatement.setInt(1,ex.getID());
            preparedStatement.setString(2,ex.getUsername());
            preparedStatement.setString(3,ex.getPassword());
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
        logger.traceEntry("deleting employee with {}",ID);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from employee where ID=?")){
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

    public int update(Integer ID, Employee entity) {
        logger.traceEntry("Update a person with id {}",ID);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("update employee set ID=?,username=?,password=? where ID=?")){
            preStmt.setInt(1,entity.getID());
            preStmt.setString(2,entity.getUsername());
            preStmt.setString(3,entity.getPassword());
            preStmt.setInt(3,ID);
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

    public Employee findOne(int ID){
        logger.traceEntry("finding employee with id {}",ID);
        Connection con=dbUtils.getConnection();
        Employee ex=null;
        try{
            PreparedStatement preStmt=con.prepareStatement("select * from employee where ID=?");
            preStmt.setInt(1,ID);
            ResultSet rs=preStmt.executeQuery();
            while (rs.next()){
                String name = rs.getString("username");
                String passwd=rs.getString("password");
                ex = new Employee(ID,name,passwd);
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
        logger.traceExit("No employee found with this id {}",ID);
        return null;
    }


    public List<Employee> findAll() {
        Connection con=dbUtils.getConnection();
        List<Employee> excursii=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from employee")) {
            try(ResultSet rs=preStmt.executeQuery()) {
                while (rs.next()) {
                    int ID = rs.getInt("ID");
                    String name = rs.getString("username");
                    String passwd=rs.getString("password");
                    Employee ex = new Employee(ID,name,passwd);
                    excursii.add(ex);
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
        logger.traceExit(excursii);
        return excursii;
    }
}
