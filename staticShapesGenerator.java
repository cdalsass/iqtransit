/* must be outside of package due to dependency chain preventing compilation.  */
/*
javac -cp build/libs/iqtransit-all.jar staticShapesGenerator.java 
java -cp .:build/libs/iqtransit-all.jar staticShapesGenerator ./build/resources/test/config.properties
*/


import java.util.Properties;
import com.iqtransit.misc.Config;
import java.io.IOException;
import com.iqtransit.db.MySQL;
import java.sql.SQLException;
import com.iqtransit.agency.MBTAAgency;
import java.io.PrintWriter;
import java.io.FileNotFoundException; 
import java.io.UnsupportedEncodingException; 

public class staticShapesGenerator {
	
	private static java.sql.Connection getConnection(String properties) {

        Properties prop  = null;
        try {
            prop = Config.load(properties);
        } catch (IOException e) {
            System.out.println("Could not read properties file");
        }

        MySQL mysql = new MySQL("jdbc:mysql://" + prop.getProperty("dbhost") + ":3306/"  + prop.getProperty("database")  + "?user=" + prop.getProperty("dbuser") + "&password=" +prop.getProperty("dbpassword"));
        
        try {
            mysql.connect();
            System.out.println("just connected");
        } catch (Exception e) {
            System.out.println("unable to connect to database " + e.toString()); 
        }

        return mysql.getConn();
    }

    // stupid simple "convert String to java variable" 
    public static String simpleVariableIze(String variable) {
    	return variable.replace("-","_");
    }

	public static void main(String[] args) {

			int RESOLUTION = 10; /* reduce returned points to avoid code too large error (64 bytes) and increase performance on android */

		java.sql.Connection mysql = getConnection(args[0]);
		
		// need to make this multi-agency. Use agencies class to do this.
		MBTAAgency agency = new MBTAAgency();
        agency.assignConnection(mysql);
		PrintWriter writer;

        try {
        	writer = new PrintWriter("src/main/java/com/iqtransit/gen/" + agency.getId() + "StaticPathData.java" , "UTF-8");
        	
        } catch (FileNotFoundException e2) {
        	System.out.println(e2.toString());
        	return;
        } catch (UnsupportedEncodingException e3) {
			System.out.println(e3.toString());
        	return;
        }
	

		writer.println("package com.iqtransit.gen;");
		writer.println("public class " + agency.getId() + "StaticPathData {");
		writer.println("public static double[][] getLines(String route) {");

		String[] route_ids = agency.getRouteIds();

		for (int k = 0; k < route_ids.length; k++) {
			String route = route_ids[k];
		    writer.print("double [][] "  + simpleVariableIze(route) + " = ");

			try {
				writer.print("{");
				double[][] paths = agency.getLinePaths(route);
		        double[][]  deduped_paths = agency.removeDuplicateShapes(paths);
		        
		        for (int i = 0; i < deduped_paths.length; i++) {
		        	writer.println("{");


					for (int j = 0; j < deduped_paths[i].length; j++) {
						
						if (j == 0 || j % RESOLUTION == 0 || j == deduped_paths[i].length - 1) {
							writer.print(Double.toString(deduped_paths[i][j]));	
							if (j != deduped_paths[i].length - 1) {
									writer.print(",");
							}
						}

					}
					writer.println("}");
					if (i != deduped_paths.length - 1) {
							writer.print(",");
					}
				}
				

			} catch (SQLException e) {
				writer.println(e.toString());
			}
			writer.println("};");
		}

		for (int k = 0; k < route_ids.length; k++) {
			writer.println("if (route.equals(\"" + route_ids[k] + "\")) {");
			writer.println("return "  + simpleVariableIze(route_ids[k]) + ";");
			writer.println("}");
		}

		writer.println("System.out.println(\"Error: no static path data for \" + route);");
		writer.println("return new double[0][0];"); // really an error condition. 

		// end function
 		writer.println("}"); // end getLines()
 		writer.println("}"); // end class declaration. 

 		writer.close();
	}


}