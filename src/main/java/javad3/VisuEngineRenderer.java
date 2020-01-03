package javad3;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import javax.json.Json;

public class VisuEngineRenderer {

	private String serverHost;
	private int serverPort;
	private HttpClient httpClient;

	public VisuEngineRenderer(String serverHost, int serverPort) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.httpClient = HttpClient.newHttpClient();
	}
	
	String getURLForChartIdUnitAndTemplate(int chartId, String unit, String template) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(serverHost).append(":").append(serverPort)
			.append("/chart/").append(chartId)
			.append("?unit=").append(unit)
			.append("&template=").append(template);

		return sb.toString();
	}

	int createChart() {
		try {
			return this.sendChartToServer();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private int sendChartToServer() throws IOException, InterruptedException {
		HttpRequest request = this.createRequestForNewChart();
		HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
		
		if (response.statusCode() != 200) {
			return -1;
		}

		return Integer.parseInt(response.headers().firstValue("location").orElse("-1"));
	}

	private HttpRequest createRequestForNewChart() throws IOException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(String.format("http://%s:%d/chart", this.serverHost, this.serverPort)))
				.header("Content-Type", "application/json; utf-8")
				.POST(BodyPublishers.noBody())
				.build();

		return request;
	}

	boolean setOption(int chartId, String option, String value) {
		String body = Json.createObjectBuilder().add(option, value).build().toString();
		return sendData(chartId, body);
	}

	boolean sendData(int chartId, String data) {
		try {
			HttpRequest request = createRequestToSendData(chartId, data);
			HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
			
			return response.statusCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private HttpRequest createRequestToSendData(int chartId, String body) throws IOException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(String.format("http://%s:%d/chart/%d", serverHost, serverPort, chartId)))
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(body))
				.build();

		return request;
	}
}
