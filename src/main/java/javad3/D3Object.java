package javad3;

abstract class D3Object {
	
	private static final String OPTION_WIDTH = "width";
	private static final String OPTION_HEIGHT = "height";
	private static final String OPTION_TITLE = "title";
	
	private int id;
	private String unit;
	private String template;
	
	private VisuEngineRenderer renderer;
	
	D3Object(VisuEngineRenderer visuEngineRenderer, String unit, String template) {
		this.id = visuEngineRenderer.createChart();
		this.renderer = visuEngineRenderer;
		this.unit = unit;
		this.template = template;
	}
	
	public void setWidth(int width) {
		setOption(OPTION_WIDTH, Integer.toString(width));
	}
	
	public void setHeight(int height) {
		setOption(OPTION_HEIGHT, Integer.toString(height));
	}
	
	public void setTitle(String title) {
		setOption(OPTION_TITLE, title);
	}
	
	public String getLocation() {
		return this.renderer.getURLForChartIdUnitAndTemplate(this.id, unit, template);
	}
	
	void sendData(String data) {
		this.renderer.sendData(this.id, data);
	}
	
	void setOption(String unit, String template) {
		this.renderer.setOption(this.id, unit, template);
	}
	
	public int getCountDatapoints() {
		return 0;
	}
	
}