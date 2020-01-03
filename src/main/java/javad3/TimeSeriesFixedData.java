package javad3;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

public class TimeSeriesFixedData extends TimeSeries {
	
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SS");
	
	private final static String TEMPLATE = "timeseries-fixed-data";
	private final static String UNIT = "d3js";
	
	private final static String OPTION_DATA_KEY = "data";
	private final static String OPTION_DATA_ENTRY_KEY = "date";
	private final static String OPTION_DATA_ENTRY_VALUE = "value";
	private static final String OPTION_DATA_REFRESH_INTERVAL = "dataRefreshInterval";
	private static final String OPTION_VISIBLE_DATAPOINTS = "visibleDatapointsLimit";
	
	private List<TimeSeriesData> data;

	public TimeSeriesFixedData(VisuEngineRenderer renderer) {
		super(renderer, UNIT, TEMPLATE);
		this.data = new ArrayList<TimeSeriesData>();
	}
	
	@Override
	public void addData(List<TimeSeriesData> data) {
		JsonArrayBuilder jsonDataArray = Json.createArrayBuilder();
		
		for(TimeSeriesData entry : data) {
			jsonDataArray = jsonDataArray.add(Json.createObjectBuilder().add(OPTION_DATA_ENTRY_KEY, entry.getKey().format(dateTimeFormatter)).add(OPTION_DATA_ENTRY_VALUE, Double.toString(entry.getValue())));
			this.data.add(entry);
		}
		
		String json = Json.createObjectBuilder().add(OPTION_DATA_KEY, jsonDataArray).build().toString();
		
		super.sendData(json);
	}
	
	@Override
	public int getCountDatapoints() {
		return this.data.size();
	}
	
	public void setDataRefreshInterval(long millis) {
		setOption(OPTION_DATA_REFRESH_INTERVAL , Long.toString(millis));
	}
	
	public void setVisibleDatapointsLimit(int limit) {
		setOption(OPTION_VISIBLE_DATAPOINTS, Integer.toString(limit));
	}
}
