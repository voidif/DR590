//import org.eclipse.jgit.api.Git;

	import java.sql.*;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Map;

	/**
	 * Created by Hina Fatima on 3/25/2018.
	 * Edit 1: Akshay Nataraju : 10/18/2018
	 */
	public class DBConnection {
	    static final String JDBC_DRIVER = "org.postgresql.Driver";
	    static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";

	    //  Database credentials
	    static final String USER = "postgres";
	    static final String PASS = "1234";
	    
	    public void createschemawithtables() {
	    	 Connection conn = null;
		        Statement stmt = null;
		        try{
		        	
		            //Register JDBC driver
		            Class.forName(JDBC_DRIVER);

		            //Open a connection
		            //System.out.println("Connecting to database...");
		            conn = DriverManager.getConnection(DB_URL, USER, PASS);
		            
		            String schemaname = null;
		            int sonar_project = 0;

		            Map<String, Integer> existingtables = new HashMap<>();
		            existingtables.put("sonar_project", 0);
		            existingtables.put("sonar_project_issues", 0);
		            existingtables.put("sonar_project_metrics", 0);

		            //checking whether sonarqube schema exists 
		            stmt = conn.createStatement();
		            String checkschemaexists = "select nspname from pg_catalog.pg_namespace where nspname = 'sonarqube'";
		            ResultSet rs = stmt.executeQuery(checkschemaexists);
		            
		            //there might be many schemas, checking if sonarqube is present within the list
		            while ( rs.next() ) {
		                schemaname = rs.getString("nspname");
		                if(schemaname.equals("sonarqube")) {
		                	break;
		                }
		            }
		            rs.close(); 
		            
		            //if sonarqube not present, create it
		            if(schemaname == null || !schemaname.equals("sonarqube")) {
		            	String schemascript = "Create schema sonarqube";
		            	stmt.execute(schemascript);
		            }
		            
		            //getting list of all tables in sonarqube schema
		            String getalltables = "SELECT tablename FROM pg_catalog.pg_tables where schemaname = 'sonarqube'";
		            ResultSet alltables = stmt.executeQuery(getalltables);
		            while ( alltables.next() ) {
		            	if(existingtables.get(alltables.getString("tablename")) == 0) {
		            		existingtables.put(alltables.getString("tablename"), 1);
		            		sonar_project = 1;
		            	}
		            }
		            rs.close();
		         
		            //creating the 3 required tables when they are not present in the schema
		            if(sonar_project == 0) {
		            		createspecifictable("sonar_projecttablescript");
		            		createspecifictable("sonar_project_issuestablescript");
		            		createspecifictable("sonar_project_metricstablescript");
		            }
		            	
		        }
		        catch(Exception e) {
		        	System.out.println(e);
		        }

		                    
	    }

	    //create table scripts for the 3 required tables
	    private void createspecifictable(String key) {
	    	Connection conn = null;
	        Statement stmt = null;
	        try{
	        	
	            //Register JDBC driver
	            Class.forName(JDBC_DRIVER);
	            String sql = null;
	            //Open a connection
	            //System.out.println("Connecting to database...");
	            conn = DriverManager.getConnection(DB_URL, USER, PASS);
	            stmt = conn.createStatement();
	            if(key.equals("sonar_projecttablescript")) {
             sql = "CREATE TABLE sonarqube.sonar_project " +
            		"(id text unique," + 
            		"name text," + 
            		"key text unique,"	+ 
            		"primary key (id, key))";
	            }
	            else if(key.equals("sonar_project_issuestablescript")){
             sql = "CREATE TABLE sonarqube.sonar_project_issues" + 
            		"(" + 
            		"    key text," + 
            		"    project text," + 
            		"    rule text," + 
            		"    rule_name text," + 
            		"    flows text," + 
            		"    status text," + 
            		"    message text," + 
            		"    author text," + 
            		"    tags text," + 
            		"    creation_date text," + 
            		"    update_date text," + 
            		"    severity text," + 
            		"    type text," + 
            		"    effort text," + 
            		"    effort_days text," + 
            		"    effort_hours text," + 
            		"    effort_minutes text," + 
            		"    debt text," + 
            		"    component text," + 
            		"    \"startLine\" text," + 
            		"    \"endLine\" text," + 
            		"    \"startOffset\" text," + 
            		"    \"endOffset\" text,"+
            		"    primary key(key)," +
            		"    foreign key(project) references sonarqube.sonar_project(key)" +
            		")";
	            }
	            else if(key.equals("sonar_project_metricstablescript")) {
             sql = "CREATE TABLE sonarqube.sonar_project_metrics"+ 
            		"(" + 
            		"id text," + 
            		"    blocker_violations text," + 
            		"    files text," + 
            		"    functions text," + 
            		"    minor_violations text," + 
            		"    bugs text," + 
            		"    ncloc_language_distribution text," + 
            		"    directories text," + 
            		"    code_smells text," + 
            		"    duplicated_blocks text," + 
            		"    major_violations text," + 
            		"    duplicated_lines_density text," + 
            		"    sqale_rating text," + 
            		"    ncloc text," + 
            		"    lines text," + 
            		"    vulnerabilities text," + 
            		"    info_violations text," + 
            		"    sqale_debt_ratio text," + 
            		"    complexity text," + 
            		"    violations text," + 
            		"    comment_lines_density text," + 
            		"    statements text," + 
            		"    duplicated_files text," + 
            		"    reliability_rating text," + 
            		"    sqale_index text," + 
            		"    critical_violations text," + 
            		"    reliability_remediation_effort text," + 
            		"    security_remediation_effort text," + 
            		"    security_rating text," + 
            		"    duplicated_lines text," + 
            		"    file_complexity text," + 
            		"    test_success_density text," + 
            		"    primary key(id)," + 
            		"    foreign key(id) references sonarqube.sonar_project(id)" + 
            		")";
	            }
            	stmt.executeUpdate(sql);
            	
	        }
	        catch(SQLException s) {
	        	System.err.println( s.getClass().getName()+": "+ s.getMessage() );
	        	System.out.println(s);
	        }
	        catch(Exception e) {
	        	System.out.println(e);
	        }
			
		}

	  //insert measure and project info to sql tables
	    public void InsertMeasure(ArrayList<SonarHTTP.Project> project, ArrayList<SonarHTTP.Measure> measure) {
	        Connection conn = null;
	        Statement stmt = null;
	        try{
	            //Register JDBC driver
	            Class.forName(JDBC_DRIVER);
	            conn = DriverManager.getConnection(DB_URL, USER, PASS);
	            stmt = conn.createStatement();
	            //inserts individual project details into the sonar_project table
	            for(int i=0; i<project.size();i++){
	                    String sql= "INSERT INTO sonarqube.sonar_project (\"id\", \"name\", \"key\")"
	                            + " select '"+project.get(i).getId()+"','"+project.get(i).getName()+"','"+project.get(i).getKey()+"' WHERE NOT EXISTS (SELECT 1 FROM sonarqube.sonar_project WHERE id='"+project.get(i).getId()+"')";
	                    stmt.executeUpdate(sql);
	            }
	            stmt.close();
	            conn.close();
	            conn = DriverManager.getConnection(DB_URL, USER, PASS);
	            stmt = conn.createStatement();
	            //inserts metrics info to the sonar_project_metrics table
	            for(int i=0;i<measure.size();i++){
	                String table = "sonarqube.sonar_project_metrics";
	                String str = String.join("\", \"",measure.get(i).getMetric());
	                String str_metric=("\"id\"")+", "+("\""+str+"\"");
	                String str_next = String.join("\', \'",measure.get(i).getValue());
	                String str_value=("'"+project.get(i).getId()+"'")+","+("\'"+str_next+"\'");
	                String sql = "Insert into "+table+" ("+str_metric+") select "+str_value+" WHERE NOT EXISTS (SELECT 1 FROM "+table+ " WHERE id='"+project.get(i).getId()+"')";
	                stmt.executeUpdate(sql);
	            }
	            stmt.close();
	            conn.close();
	        }catch(SQLException se){
	            //Handle errors for JDBC
	            se.printStackTrace();
	        }catch(Exception e){
	            //Handle errors for Class.forName
	            e.printStackTrace();
	        }finally{
	            //finally block used to close resources
	            try{
	                if(stmt!=null)
	                    stmt.close();
	            }catch(SQLException se2){
	            }// nothing we can do
	            try{
	                if(conn!=null)
	                    conn.close();
	            }catch(SQLException se){
	                se.printStackTrace();
	            }//end finally try
	        }//end try
	        //System.out.println("Goodbye!");
	    }
	    
	    //insert issues to sql table
	    public void InsertIssues(ArrayList<SonarHTTP.Issues> issues) {
	        Connection conn = null;
	        Statement stmt = null;
	        try{
	            //Register JDBC driver
	            Class.forName(JDBC_DRIVER);
	            //Open a connection
	            //System.out.println("Connecting to database...");
	            conn = DriverManager.getConnection(DB_URL, USER, PASS);
	            stmt = conn.createStatement();
	            //insert all issues to sonar_project_issues table
	            for(int i=0; i<issues.size();i++){
	                String table = "sonarqube.sonar_project_issues";
	                String sql = "INSERT INTO "+table+" (\"key\", \"project\", \"component\", \"rule\", \"rule_name\", \"startLine\", \"endLine\", \"startOffset\", \"endOffset\",\"flows\",\"status\", \"message\", \"author\",\"tags\",\"creation_date\",\"update_date\",\"severity\",\"type\",\"effort\", \"effort_days\", \"effort_hours\", \"effort_minutes\",\"debt\")"
	                        + " select '"+issues.get(i).geKey()+"','"+issues.get(i).getProject()+"','"+issues.get(i).getComponent()+"','"+issues.get(i).getRule()+"', '"+issues.get(i).getRule_Name()+"', '"+issues.get(i).getStartLine()+"', '"+issues.get(i).getEndLine()+"', '"+issues.get(i).getStartOffset()+"', '"+issues.get(i).getEndOffset()+"',  "
	                        + " '"+issues.get(
	                        i).getFlows().replace("'","")+"','"+issues.get(i).getStatus()+"','"+issues.get(i).getMessage().replaceAll("[^a-zA-Z0-9]+"," ")+"','"+issues.get(i).getAuthor()+"', "
	                        + " '"+issues.get(i).getTags()+"','"+issues.get(i).getCreationDate()+"','"+issues.get(i).getUpdateDate()+"','"+issues.get(i).getSeverity()+"','"+issues.get(i).getType()+"','"+issues.get(i).getEffort()+"', '"+issues.get(i).getEffortDays()+"', '"+issues.get(i).getEffortHours()+"', '"+issues.get(i).getEffortMinutes()+"','"+issues.get(i).getDebt()+"' "
	                        + " WHERE NOT EXISTS (SELECT 1 FROM "+table+" WHERE key='"+issues.get(i).geKey()+"')";
	                stmt.executeUpdate(sql);
	            }
	            stmt.close();
	            //conn.commit();
	            conn.close();
	        }catch(SQLException se){
	            //Handle errors for JDBC
	            se.printStackTrace();
	        }catch(Exception e){
	            //Handle errors for Class.forName
	            e.printStackTrace();
	        }finally{
	            //finally block used to close resources
	            try{
	                if(stmt!=null)
	                    stmt.close();
	            }catch(SQLException se2){
	            }// nothing we can do
	            try{
	                if(conn!=null)
	                    conn.close();
	            }catch(SQLException se){
	                se.printStackTrace();
	            }//end finally try
	        }//end try
	        //System.out.println("Goodbye!");
	    }
	}
