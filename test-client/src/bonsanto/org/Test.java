package bonsanto.org;


import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@WebService()
public class Test {
	private static Dispatcher d;

	@WebMethod
	public void startTest(int intensity) {
		ArrayList<Object> parameters = new ArrayList<>();
		Properties p = new Properties();
		long[] times = new long[4];
		InputStream stream;


		try {
			stream = new FileInputStream("queries.properties");
			p.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}


		//SELECT * FROM pokemon ORDER BY id_pokemon ASC
		String[] selectQuery = p.getProperty("selectQuery").split(",");
		String db = selectQuery[0],
				query = selectQuery[1];

		//Get the time when the select test starts.
		times[0] = System.nanoTime();

		for (int i = 0; i < intensity; i++) {
			try {
				d.queryJSON(db, query, parameters);
			} catch (IOException_Exception e) {
				e.printStackTrace();
			}
		}

		//Get the time that the select test took.
		times[0] = System.nanoTime() - times[0];

		//INSERT tests.
		String[] insertQuery = p.getProperty("insertQuery").split(",");
		db = insertQuery[0];
		query = insertQuery[1];

		//INSERT INTO pokemon VALUES(?,?) has 2 parameters.
		int[] randomNumbers = new int[intensity];
		String randomName = "TestName";

		//Generate Random integers to attempt to insert to the database
		for (int i = 0; i < randomNumbers.length; i++) {
			randomNumbers[i] = new Random().nextInt(500000);
		}

		//Get the time when the insert test starts.
		times[1] = System.nanoTime();

		for (int randomNumber : randomNumbers) {
			try {
				parameters.clear();
				parameters.add(randomNumber);
				parameters.add(randomName);
				d.queryJSON(db, query, parameters);
			} catch (IOException_Exception e) {
				e.printStackTrace();
			}
		}

		//Get the time that the insert test took.
		times[1] = System.nanoTime() - times[1];

		//UPDATE pokemon SET na_pokemon = ? WHERE id_pokemon = ?
		String[] updateQuery = p.getProperty("updateQuery").split(",");
		db = updateQuery[0];
		query = updateQuery[1];

		//Get the time when the update test starts.
		times[2] = System.nanoTime();

		for (int randomNumber : randomNumbers) {
			try {
				parameters.clear();
				parameters.add(randomName + randomNumber);
				parameters.add(randomNumber);
				d.queryJSON(db, query, parameters);
			} catch (IOException_Exception e) {
				e.printStackTrace();
			}
		}

		//Get the time that the update test took.
		times[2] = System.nanoTime() - times[2];

		//DELETE FROM pokemon WHERE id_pokemon=?
		String[] deleteQuery = p.getProperty("deleteQuery").split(",");
		db = deleteQuery[0];
		query = deleteQuery[1];

		//Get the time when the delete test starts.
		times[3] = System.nanoTime();

		for (int randomNumber : randomNumbers) {
			try {
				parameters.clear();
				parameters.add(randomNumber);
				d.queryJSON(db, query, parameters);
			} catch (IOException_Exception e) {
				e.printStackTrace();
			}
		}

		//Get the time that the delete test took.
		times[3] = System.nanoTime() - times[3];

		//Total time
		long result = 0;
		for (long l : times) result += l;

		try {
			Calendar cal = Calendar.getInstance();
			CSVWriter csvWriter = new CSVWriter(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) +
					"-" + cal.get(Calendar.DATE) + "-" + new Random().nextInt(5000) + "testresult.csv");
			csvWriter.append("SELECT queryJSON TEST", String.valueOf((times[0] / 1000000000.0)));
			csvWriter.append("INSERT queryJSON TEST", String.valueOf((times[1] / 1000000000.0)));
			csvWriter.append("UPDATE queryJSON TEST", String.valueOf((times[2] / 1000000000.0)));
			csvWriter.append("DELETE queryJSON TEST", String.valueOf((times[3] / 1000000000.0)));
			csvWriter.append("RESULT queryJSON TEST", String.valueOf((result / 1000000000.0)));
			csvWriter.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) {
		Properties endpoint = new Properties();
		Object implementor = new Test();
		FileInputStream inputStream;
		String address = "http://0.0.0.0:8000/Test";
		System.out.println("http://localhost:8000/Test");

		try {
			inputStream = new FileInputStream("endpoint.properties");
			endpoint.load(inputStream);

			//Get the endpoint url and set it to the webservice client
			String url = endpoint.getProperty("endpoint");
			d = new DispatcherService(new URL(url)).getDispatcherPort();

			//Start the Dispatcher Server.
			Endpoint.publish(address, implementor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
