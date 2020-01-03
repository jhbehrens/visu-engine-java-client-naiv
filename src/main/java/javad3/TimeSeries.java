package javad3;

import java.time.LocalDateTime;
import java.util.List;

public abstract class TimeSeries extends D3Object {
	
	TimeSeries(VisuEngineRenderer renderer, String unit, String template) {
		super(renderer, unit, template);
	}

	public static class TimeSeriesData {
		private LocalDateTime key;
		private double value;
		
		public TimeSeriesData(LocalDateTime key, double value) {
			this.key = key;
			this.value = value;
		}
		
		public LocalDateTime getKey() {
			return this.key;
		}
		
		public double getValue() {
			return this.value;
		}
	}
	
	public abstract void addData(List<TimeSeriesData> data);
	
}