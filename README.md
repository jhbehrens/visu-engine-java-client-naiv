# JavaD3
Making beautiful plots from Java using VisuEngine d3js.

Tested on Windows 7 with FFmpeg 4.2, PhantomJS 2.1.1

## Prerequisites

1. Make sure you have an instance of VisuEngine (https://github.com/uniflo/VisuEngine) running and it has the d3js unit installed (https://github.com/uniflo/d3js).
2. Make sure the needed ejs templates are in the d3js unit. The ejs templates for this project are inside the ejs folder.
3. You need to download the PhantomJS 2.1.1 and FFmpeg 4.2 executables and put them in the video folder

## Usage

### Example Timeseries Plot

```Java
VisuEngineRenderer renderer = new VisuEngineRenderer("localhost", 8000);
		
TimeSeriesFixedData timeseries = new TimeSeriesFixedData(renderer);

List<TimeSeriesData> data = new ArrayList<>();

timeseries.addData(data);
		
VideoCreator vc = new VideoCreator(timeseries);
		
vc.createVideo();
```

Depending on the length of the video, it can take a while until production is done.
You can find the video under "video/output.mp4".

## How to add your own Time Series - Template

1. Create an ejs template for the VisuEngine and make it available (See VisuEngine README).
2. In order to create videos the template needs to have a button with id "start" that starts the animation.
3. The visible area in the video is everything inside the element with id "chart", so make sure you assign the id to the SVG created by D3.

## How to add your own Time Series - Java

1. Create an implementation of the abstract class <code>TimeSeries</code> (See javad3/TimeSeriesFixedData.java)
2. Implement the <code>addData</code> method in a way that it creates a JSON as your template expects.
3. Override the <code>getCountDatapoints</code> method in a way that it returns the number of current datapoints. This is just used in a minor optimization. Not overriding does not effect correctness.
