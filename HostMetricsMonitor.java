package eBay;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HostMetricsMonitor {
	
	private static final String url = "http://host/metrics";
	private static Map<String, String> responses = new HashMap<>();

	public static void main(String[] args) {
		/*
		 * Given the input list of all the host names 
		 */
		
		List<String> hostNameList = new ArrayList<String>();
		hostNameList.add("host1");
		hostNameList.add("host2");
		hostNameList.add("host3");
		hostNameList.add("host4");
		hostNameList.add("host5");
		
		/*
		 * Leveraging the ExecutorService to utilizes a pool of threads 
		 */
		ExecutorService executor = Executors.newCachedThreadPool();
		Runnable runMonitor = new Runnable() {
			public void run() {
				MetricsUpdater(executor, hostNameList);
			}
		};
		
		/*
		 * Utilizing the ScheduledExecutorService to run the MetricUpdater every 2 seconds
		 * This will be running until it is killed. Can write a time limit to execute the program for the time duration.
		 */
		ScheduledExecutorService MonitoringService = Executors.newSingleThreadScheduledExecutor();
		MonitoringService.scheduleAtFixedRate(runMonitor, 0, 2, TimeUnit.SECONDS);
		
//		executor.shutdown();
//		// Wait until all threads are finish
//		while (!executor.isTerminated()) {
// 
//		}
		System.out.println("\nDone retreiving metrics\n");
	}
	
	/*
	 * Parse the input host names list and create the url for each host and executing the get request
	 */
	private static void MetricsUpdater(ExecutorService executor, List<String> hostNameList) {
		for (int i = 0; i < hostNameList.size(); i++) {
			String hostName = hostNameList.get(i);
			Runnable worker = new MyRunnable(url.replace("host", hostName), hostName);
			executor.execute(worker);
		}
	}
	
	
	
	public static class MyRunnable implements Runnable {
		private final String url;
		private final String hostName;
 
		MyRunnable(String url, String hostName) {
			this.url = url;
			this.hostName = hostName;
		}
 
		@Override
		public void run() {
 
			String result = "";
			try {
				URL hostURL = new URL("www.google.com");
				HttpURLConnection connection = (HttpURLConnection) hostURL.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(300);
				connection.connect();
 
				int code = connection.getResponseCode();
				
				if (code == 200) {
					InputStream is = connection.getInputStream();
					result = new String(is.readAllBytes());
					is.close();
					System.out.println("result:" + result);
					responses.put(hostName, result);
				} else {
					// Handling case 
					System.out.println("Failed to retreive metrics from" + url);
				}
			} catch (Exception e) {
				result = "Exception: " + e.getMessage();
 
			}
		}
	}
	
	/*
	 * Retrieve the metrics by the host name provided
	 */
	public static String RetreiveMetricsByHost(String HostName) {
		return responses.get(HostName);
	}

}
