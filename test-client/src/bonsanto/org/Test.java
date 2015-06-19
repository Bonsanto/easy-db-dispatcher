package bonsanto.org;


import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

@WebService()
public class Test {
	private static Dispatcher d;

	@WebMethod
	public void startTest(int intensity) {
		System.out.println("Test Started on " + new Date());


		ArrayList<Object> parameters = new ArrayList<>();
		Properties p = new Properties();
		long[] times = new long[7];
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

		//WriteJSON - SELECT * FROM pokemon ORDER BY id_pokemon ASC
		String[] writeJSON = p.getProperty("selectQuery").split(",");
		db = writeJSON[0];
		query = writeJSON[1];
		parameters.clear();

		//Get the time when the select test starts.
		times[4] = System.nanoTime();

		for (int randomNumber : randomNumbers) {
			try {
				d.writeJSON(db, query, String.valueOf(randomNumber) + ".json", parameters);
			} catch (IOException_Exception e) {
				e.printStackTrace();
			}
		}

		//Get the time that the select test took.
		times[4] = System.nanoTime() - times[4];


		//writeCompleteCSV - SELECT * FROM pokemon ORDER BY id_pokemon ASC
		String[] writeCSV = p.getProperty("selectQuery").split(",");
		db = writeCSV[0];
		query = writeCSV[1];
		parameters.clear();

		//Get the time when the select test starts.
		times[5] = System.nanoTime();

		for (int randomNumber : randomNumbers) {
			try {
				d.writeEntireCSV(db, query, String.valueOf(randomNumber) + ".csv", parameters);
			} catch (IOException_Exception e) {
				e.printStackTrace();
			}
		}

		//Get the time that the select test took.
		times[5] = System.nanoTime() - times[5];

		//queryDataTable - SELECT * FROM pokemon ORDER BY id_pokemon ASC
		String[] queryDateTable = p.getProperty("selectQuery").split(",");
		db = queryDateTable[0];
		query = queryDateTable[1];
		parameters.clear();

		//Get the time when the select test starts.
		times[6] = System.nanoTime();

		for (int randomNumber : randomNumbers) {
			try {
				DataTable dt =  d.queryDataTable(db, query, parameters);
			} catch (IOException_Exception e) {
				e.printStackTrace();
			}
		}

		//Get the time that the select test took.
		times[6] = System.nanoTime() - times[6];

		//Total time
		long result = 0;
		for (long l : times) result += l;

		try {
			Calendar cal = Calendar.getInstance();
			CSVWriter csvWriter = new CSVWriter(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) +
					"-" + cal.get(Calendar.DATE) + "-" + new Random().nextInt(5000) + "testresult.csv");
			csvWriter.append("test_type", "test_milliseconds");
			csvWriter.append("SELECT queryJSON TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(times[0], TimeUnit.NANOSECONDS)));
			csvWriter.append("INSERT queryJSON TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(times[1], TimeUnit.NANOSECONDS)));
			csvWriter.append("UPDATE queryJSON TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(times[2], TimeUnit.NANOSECONDS)));
			csvWriter.append("DELETE queryJSON TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(times[3], TimeUnit.NANOSECONDS)));
			csvWriter.append("SELECT writeJSON TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(times[4], TimeUnit.NANOSECONDS)));
			csvWriter.append("SELECT writeEntireCSV TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(times[5], TimeUnit.NANOSECONDS)));
			csvWriter.append("SELECT queryDataTable TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(times[6], TimeUnit.NANOSECONDS)));
			csvWriter.append("RESULT queryJSON TEST", String.valueOf(TimeUnit.MILLISECONDS.convert(result, TimeUnit.NANOSECONDS)));
			csvWriter.write();
			System.out.println("Test Finished on " + new Date());
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
