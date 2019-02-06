import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hina Fatima on 3/29/2018.
 * Edit 1: Akshay Nataraju : 10/18/2018
 */
public class SonarHTTP {
	//private static int cumulativesum = 0;
	
	//counter while adding projects to DB
	private static int projectcount = 1;
	
	//hashmap to store already seen rules to id mapping using the API to get the rules. 
	private static Map<String, String> rulemap = new HashMap<String, String>();
	
    DBConnection dbConnectionObj = new DBConnection();
	
    /*method to separate days, hours and minutes in effort*/
	private Map<String, String> correcteffort(String effort) {
		Map<String, String> hm = new HashMap<>(); 
		String effort_days = "";
		String effort_hours = "";
		String effort_minutes = "";
		char[] eff = effort.toCharArray();
		if(effort.indexOf("min") != -1) {
			int minindex = effort.indexOf("min");
			for(int i = minindex - 1; i >= 0; i--) {
				if(Character.isDigit(eff[i])) {
					effort_minutes = eff[i] + effort_minutes;
				}
				if(Character.isAlphabetic(eff[i])) {
					break;
				}
				
			}
		}
		if(effort.indexOf('h') != -1) {
			int hindex = effort.indexOf('h');
			for(int i = hindex - 1; i >= 0; i--) {
				if(Character.isDigit(eff[i])) {
					effort_hours = eff[i] + effort_hours;
				}
				if(Character.isAlphabetic(eff[i])) {
					break;
				}
				
			}
		}
		if(effort.indexOf('d') != -1) {
			int dindex = effort.indexOf('d');
			for(int i = dindex - 1; i >= 0; i--) {
				if(Character.isDigit(eff[i])) {
					effort_days = eff[i] + effort_days;
				}
				if(Character.isAlphabetic(eff[i])) {
					break;
				}
				
			}
		}
		if(effort_minutes.equals("")) {
			effort_minutes = "0";
		}
		if(effort_hours.equals("")) {
			effort_hours = "0";
		}
		if(effort_days.equals("")) {
			effort_days = "0";
		}
		else {

			//System.out.println(effort_days +" "+ effort_hours +" "+ effort_minutes);
		}
		
		hm.put("effort_minutes", effort_minutes);
		hm.put("effort_hours", effort_hours);
		hm.put("effort_days", effort_days);
		return hm;
	}

	/*all variables related to a project for sonar_project table*/
    class Project {
        private String id;
        private String name;
        private String key;
        private String type;

        public Project(String id, String name, String key, String type) {
            this.id = id;
            this.name = name;
            this.key = key;
            this.type = type;

        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }

        public String getType() { return type; }
    }

    //stores all measures for a project for sonar_project_measures table
    class Measure {

        private ArrayList<String> metric;
        private ArrayList<String> value;
        private String type;

        public Measure(ArrayList<String> metric, ArrayList<String>value, String type) {

            this.metric=metric;
            this.value=value;
            this.type = type;
        }

        public ArrayList<String> getMetric(){
            return metric;
        }

        public ArrayList<String> getValue(){
            return value;
        }

        public String getType() { return type; }

    }

    //stores all details of an issue for sonar_project_issues table
    class Issues {

        private String key;
        private String component;
        private String project;
        private String rule;
        private String rule_name;
        private String startLine;
        private String endLine;
        private String startOffset;
        private String endOffset;
        private String text_range;
        private String flows;
        private String status;
        private String message;
        private String author;
        private String tags;
        private String creation_date;
        private String update_date;
        private String severity;
        private String type;
        private String file_type;
        private String effort;
        private String effort_days;
        private String effort_hours;
        private String effort_minutes;
        private String debt;


        public Issues(String key, String component, String project, String rule, String rule_name, String startLine, String endLine, String startOffset, String endOffset, String text_range, String flows, String status, String message, String author,String tags, String creation_date, String update_date, String severity, String type, String file_type, String effort, String effort_days, String effort_hours, String effort_minutes, String debt) {
            this.key=key;
            this.component=component;
            this.project=project;
            this.rule=rule;
            this.rule_name = rule_name;
            this.startLine=startLine;
            this.endLine=endLine;
            this.startOffset=startOffset;
            this.endOffset=endOffset;
            this.text_range=text_range;
            this.flows=flows;
            this.status=status;
            this.message=message;
            this.author=author;
            this.tags=tags;
            this.creation_date=creation_date;
            this.update_date=update_date;
            this.severity=severity;
            this.type=type;
            this.file_type=file_type;
            this.effort = effort;
            this.effort_days = effort_days;
            this.effort_hours = effort_hours;
            this.effort_minutes = effort_minutes;
            this.debt=debt;
        }
        public String geKey() { return key; }

        public String getComponent() { return component; }

        public String getProject() {
            return project;
        }

        public String getRule() {
            return rule;
        }
        
        public String getRule_Name() {
            return rule_name;
        }

        public String getStartLine() {
            return startLine;
        }
        
        public String getStartOffset() {
            return startOffset;
        }
        public String getEndLine() {
            return endLine;
        }
        public String getEndOffset() {
            return endOffset;
        }

        public String getTextRange() {
            return text_range;
        }

        public String getFlows() { return flows; }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getAuthor() {
            return author;
        }

        public String getTags() {
            return tags;
        }

        public String getCreationDate() {
            return creation_date;
        }

        public String getUpdateDate() {
            return update_date;
        }

        public String getSeverity() {
            return severity;
        }

        public String getType() { return type; }

        public String getFile_type() { return file_type; }

        public String getEffort() {
            return effort;
        }
        public String getEffortDays() {
            return effort_days;
        }
        public String getEffortHours() {
            return effort_hours;
        }
        public String getEffortMinutes() {
            return effort_minutes;
        }

        public String getDebt() {
            return debt;
        }
    }

    //to store JSON keys from API call results
    class ComponentId{
        private String comp_id;
        private String comp_type;

        public ComponentId(String comp_id, String comp_type){
            this.comp_id=comp_id;
            this.comp_type=comp_type;
        }

        public String getComp_id(){
            return comp_id;
        }

        public String getComp_type(){
            return comp_type;
        }


    }

    //to store JSON values from API call results
    class ComponentKey{
        private String comp_key;
        private String comp_type;

        public ComponentKey(String comp_key, String comp_type){
            this.comp_key=comp_key;
            this.comp_type=comp_type;
        }

        public String getComp_key(){
            return comp_key;
        }

        public String getComp_type(){
            return comp_type;
        }


    }

    /*Gets the individual project details and adds it to the Component class
     * and then calls getMeasures and getIssues methods*/
    public void getComponent() throws IOException {
        List<ComponentId> componentId = new ArrayList<>();
        List<ComponentKey> componentKey = new ArrayList<>();
        String response;
        dbConnectionObj.createschemawithtables();
        //List<String> keys = new ArrayList<>();
        List<String> keys = readprojectkeys();
        //keys.add("org.apache.avro:avro-toplevel");
        for (int i = 0; i < keys.size(); i++) {
            String component_uri= ("http://localhost:9000/api/components/show?key=" + keys.get(i)).split(",")[0];
            String component_type=("http://localhost:9000/api/components/show?key=" + keys.get(i)).split(",")[0];
            response = getHTTPConnection(component_uri);
            JSONObject jsonObject = new JSONObject(response);
            componentId.add(new ComponentId(((JSONObject) jsonObject.get("component")).get("id").toString(), component_type));
            componentKey.add(new ComponentKey(((JSONObject) jsonObject.get("component")).get("key").toString(), component_type));
        }
//        getMeasures(componentId);
        getIssues(componentKey);

    }

    /*method to get the list of all projects that are analyzed in sonarqube, along with
     * their key using TRK*/
    private List<String> readprojectkeys() throws ClientProtocolException, IOException {
    	Map<String, String> projectdetailsmap = new HashMap<>();
    	List<String> arr = new ArrayList<>();
    	
		String conn = "http://localhost:9000/api/components/search?qualifiers=TRK";
		String response = getHTTPConnection(conn);
		JSONObject jsonObject = new JSONObject(response);
		JSONArray componentslist = (JSONArray) jsonObject.get("components");
		for(Object comp: componentslist) {
			String key = ((JSONObject) comp).getString("key").toString();
	        String name = ((JSONObject) comp).getString("name").toString();
	        arr.add(key);
	        projectdetailsmap.put(name, key);
		}
		return arr;
	}

    //method to fetch all measures from API and then initiate storage to sql
	public void getMeasures(List<ComponentId> componentId) throws IOException {
        ArrayList<Project> project = new ArrayList<>();
        ArrayList<Measure> measure = new ArrayList<>();
        String component_uri, response;
        String id;
        String key;
        String name;
        ArrayList<String> metric;
        ArrayList<String> value;
        for (int i = 0; i < componentId.size(); i++) {
            component_uri = "http://localhost:9000/api/measures/component?componentId=" + componentId.get(i).comp_id + "&metricKeys=test_success_density,statements," +
                    "functions,ncloc_language_distribution,ncloc,lines,files,directories,security_remediation_effort,security_rating," +
                    "vulnerabilities,reliability_remediation_effort,reliability_rating,bugs,sqale_debt_ratio,sqale_index,sqale_rating," +
                    "code_smells,critical_violations,info_violations,blocker_violations,minor_violations,major_violations,violations," +
                    "complexity,file_complexity,comment_lines_density,duplicated_blocks,duplicated_files,duplicated_lines,duplicated_lines_density";
            response = getHTTPConnection(component_uri);
            //System.out.println(response);
            JSONObject jsonObject = new JSONObject(response);
            id = ((JSONObject) jsonObject.get("component")).get("id").toString();
            key = ((JSONObject) jsonObject.get("component")).get("key").toString();
            name = ((JSONObject) jsonObject.get("component")).get("name").toString();
            project.add(new Project(id, name, key,componentId.get(i).comp_type));

            JSONArray arrayMeasure = (JSONArray) ((JSONObject) jsonObject.get("component")).get("measures");
            metric = new ArrayList<>();
            value = new ArrayList<>();
            for (int j = 0; j < arrayMeasure.length(); j++) {

                metric.add(((JSONObject) arrayMeasure.get(j)).get("metric").toString());
                value.add(((JSONObject) arrayMeasure.get(j)).get("value").toString());
            }
            measure.add(new Measure(metric, value, componentId.get(i).comp_type));
        }

        dbConnectionObj.InsertMeasure(project, measure);
    }

	//method to fetch all issues from API and then initiate storage to sql
    public void getIssues(List<ComponentKey> componentKey) throws IOException {
        String component_uri, response;
        String[] severity = new String[]{"INFO", "MINOR", "MAJOR", "CRITICAL", "BLOCKER"};
        String[] type = new String[]{"CODE_SMELL", "BUG", "VULNERABILITY"};

        String key, project="", component="", rule="", rule_name="", startLine="", endLine="", startOffset="", endOffset="", text_range="", flows="", status="", message="",author="", tags="", issue_severity="", issue_type="", effort="",
        		effort_days="0",effort_hours="0",effort_minutes="0",debt="";
        String creation_date="", update_date="";

        JSONObject jsonObject;
        for (int m = 0; m < componentKey.size(); m++) {
            ArrayList<Issues> issues = new ArrayList<>();
            component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m).comp_key;
            response = getHTTPConnection(component_uri);
            jsonObject = new JSONObject(response);
            boolean IsIssue1000 = false;
            //int sumtotalverification = 0;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 3; j++) {
                    component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m).comp_key + "&ps=20&severities=" + severity[i] + "&types=" + type[j];
                    response = getHTTPConnection(component_uri);
                    jsonObject = new JSONObject(response);
                    int total_issues_param = (int) jsonObject.get("total");
                    //sumtotalverification += total_issues_param;
                    
                    if (total_issues_param > 10000) {
                        IsIssue1000 = true;
                        break;
                    }
                }
            }
            //cumulativesum += sumtotalverification;
            if (!IsIssue1000) {
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 3; j++) {
                        //System.out.println(i+","+j+","+severity[i]+","+type[j]);
                        component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m).comp_key + "&ps=20&severities=" + severity[i] + "&types=" + type[j];
                        response = getHTTPConnection(component_uri);
                        //System.out.println(response);
                        jsonObject = new JSONObject(response);
                        int total_issues_param = (int) jsonObject.get("total");
                        int total_pages = (int) Math.ceil((double) total_issues_param / 200);
                        for (int k = 0; k < total_pages; k++) {
                            component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m).comp_key + "&p=" + (int) (k + 1) + "&ps=200&severities=" + severity[i] + "&types=" + type[j];
                            //System.out.println(component_uri);
                            response = getHTTPConnection(component_uri);
                            jsonObject = new JSONObject(response);
                            JSONArray arrayIssues = (JSONArray) (jsonObject.get("issues"));
                            for (int l = 0; l < arrayIssues.length(); l++) {
                                key = ((JSONObject) arrayIssues.get(l)).get("key").toString();
                                project = ((JSONObject) arrayIssues.get(l)).get("project").toString();
                                component = ((JSONObject) arrayIssues.get(l)).get("component").toString();
                                rule = ((JSONObject) arrayIssues.get(l)).get("rule").toString();
                                rule_name = getrulename(rule);
                                if (((JSONObject) arrayIssues.get(l)).has("line")) {
                                }
                                if (((JSONObject) arrayIssues.get(l)).has("textRange")) {
                                    text_range = ((JSONObject) arrayIssues.get(l)).get("textRange").toString();
                                    String[] values = text_range.split(",");
                                    String v1 = null;
                                    for(String v: values) {
                                    	if(v.contains("}")) {
                                    		v1 = v.replace('}', ' ');
                                    		//System.out.println(v1);
                                    	}
                                    	
                                    	if(v.contains("endLine") || (v1 != null && v1.contains("endLine"))) {
                                    		if(v1 != null) {
                                    			String[] endl = v1.split(":");
                                        		//System.out.println(endl[1]);
                                        		endLine = endl[1];
                                    		}
                                    		else {
                                    			String[] endl = v.split(":");
                                        		//System.out.println(endl[1]);
                                        		endLine = endl[1];
                                    		}
                                    	}
                                    	else if(v.contains("endOffset") || (v1 != null && v1.contains("endOffset"))) {
                                    		
                                    		if(v1 != null) {
                                    			String[] endO = v1.split(":");
                                        		//System.out.println(endO[1]);
                                        		endOffset = endO[1];
                                    		}
                                    		else {
                                    			String[] endO = v.split(":");
                                        		//System.out.println(endO[1]);
                                        		endOffset = endO[1];
                                    		}
                                    	}
                                    	else if(v.contains("startLine") || (v1 != null && v1.contains("startLine")) ){
                                    		if(v1 != null) {
                                    			String[] startL = v1.split(":");
                                        		//System.out.println(startL[1]);
                                        		startLine = startL[1];
                                        		v1 = null;
                                    		}
                                    		else {
                                    			String[] startL = v.split(":");
                                        		//System.out.println(startL[1]);
                                        		startLine = startL[1];
                                    		}
                                    	}
                                    	else if(v.contains("startOffset") || ( v1 != null &&v1.contains("startOffset"))) {
                                    		if(v1 != null) {
                                    			String[] startO = v1.split(":");
                                        		//System.out.println(startO[1]);
                                        		startOffset = startO[1];
                                    		}
                                    		else {
                                    			String[] startO = v.split(":");
                                        		//System.out.println(startO[1]);
                                        		startOffset = startO[1];
                                    		}
                                    	}
                                    }    
                                }else {
                                	startLine = "0";
                                	startOffset = "0";
                                	endLine = "0";
                                	endOffset = "0";
                                }
                                flows = ((JSONObject) arrayIssues.get(l)).get("flows").toString();
                                status = ((JSONObject) arrayIssues.get(l)).get("status").toString();
                                message = ((JSONObject) arrayIssues.get(l)).get("message").toString();
                                author = ((JSONObject) arrayIssues.get(l)).get("author").toString();
                                tags = ((JSONObject) arrayIssues.get(l)).get("tags").toString();
                                issue_severity = ((JSONObject) arrayIssues.get(l)).get("severity").toString();
                                issue_type = ((JSONObject) arrayIssues.get(l)).get("type").toString();
                                creation_date = ((JSONObject) arrayIssues.get(l)).get("creationDate").toString();
                                update_date = ((JSONObject) arrayIssues.get(l)).get("updateDate").toString();
                                if (((JSONObject) arrayIssues.get(l)).has("effort")) {
                                    effort = ((JSONObject) arrayIssues.get(l)).get("effort").toString();
                                   Map<String, String> hm =  correcteffort(effort);
                                   //System.out.println(hm.get("effort_minutes"));
                                   effort_days = hm.get("effort_days");
                                   effort_hours = hm.get("effort_hours");
                                   effort_minutes = hm.get("effort_minutes");
                                }
                                if (((JSONObject) arrayIssues.get(l)).has("debt")) {
                                    debt = ((JSONObject) arrayIssues.get(l)).get("debt").toString();
                                }
                                issues.add(new Issues(key, component, project, rule, rule_name, startLine, endLine, startOffset, endOffset, text_range, flows, status, message, author, tags, creation_date, update_date, issue_severity, issue_type, componentKey.get(m).comp_type, effort, effort_days, effort_hours, effort_minutes, debt));
                            }
                        }
                    }
                }
            }
            dbConnectionObj.InsertIssues(issues);
            System.out.println("Working on project number: " +projectcount);
            projectcount++;
        }

    }

    /*method to build the http connection string*/
    public String getHTTPConnection(String uri) throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(uri);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet);) {
            HttpEntity entity = response.getEntity();
            String entitystring = EntityUtils.toString(entity);
            return entitystring;
        }
    }
    
    /*method to get the rule from rule name*/
    private String getrulename(String rule) throws ClientProtocolException, IOException{
    	String component_uri = "https://sonarcloud.io/api/rules/show?key=" + rule;
        String response = null;
        String name = "rule not found";
		if(!rulemap.containsKey(rule)) {
			response = getHTTPConnection(component_uri);
			JSONObject jsonObject = new JSONObject(response);
		    JSONObject arrayComponents = (JSONObject) jsonObject.get("rule");
			name = ((JSONObject) arrayComponents).getString("name").toString();
			name = name.replaceAll("'", "");
			rulemap.put(rule, name);
		}
		else {
			name = rulemap.get(rule);
		}
    	return name;
    }
    
    /*main method*/
    public static void main(String args[]) {
    	SonarHTTP shtp = new SonarHTTP();
    	try {
			shtp.getComponent();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}