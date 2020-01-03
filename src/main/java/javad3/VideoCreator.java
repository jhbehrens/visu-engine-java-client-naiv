package javad3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoCreator {
	
	private String videoBaseDirectory = "video";
	private String framesDirectory = "frames";
	private String framesPrefix = "chart";
	private int screenshotsPerInstance = 500;
	private int framerate = 25;
	private String outputFilePath = "output";
	private boolean overwriteOutput = true;
	
	private D3Object chart;
	
	public VideoCreator(D3Object chart) {
		this.chart = chart;
	}
	
	public void setScreenshotsPerInstance(int screenshotsPerInstance) {
		this.screenshotsPerInstance = screenshotsPerInstance;
	}
	
	public void setFramerate(int framerate) {
		this.framerate = framerate;
	}
	
	public void setChart(D3Object chart) {
		this.chart = chart;
	}
	
	public void setFramesDirectory(String directory) {
		this.framesDirectory = directory;
	}
	
	public void setFramesPrefix(String prefix) {
		this.framesPrefix = prefix;
	}
	
	public void setOutputFilePath(String path) {
		this.outputFilePath = path;
	}
	
	public void setOverwriteOutput(boolean overwrite) {
		this.overwriteOutput = overwrite;
	}
	
	public void createVideo() {
		this.saveHTML();
		this.makeScreenshotsFromHTML();
		this.makeVideoFromScreenshots();
	}
	
	private boolean saveHTML() {
		ProcessBuilder processBuilder;
		Process process;
		try {
			processBuilder = new ProcessBuilder();
			processBuilder.directory(new File("./" + videoBaseDirectory));
			processBuilder.command("./" + videoBaseDirectory + "/phantomjs.exe", "saveHTML.js", this.chart.getLocation(), this.framesDirectory + "/" + this.framesPrefix, Integer.toString(this.framerate));
			process = processBuilder.start();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			int test = process.waitFor();
			return test == 0;	
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean makeScreenshotsFromHTML() {
		int curr = 0;
		int start = 0;
		int max = this.screenshotsPerInstance - 1;
		boolean screenshotReturn;
		
		File file = Paths.get(videoBaseDirectory + "/" + this.framesDirectory + "/" + this.framesPrefix + Integer.toString(curr) + ".html").toFile();
		
		while(file.exists()) {
			if (curr >= max) {
				screenshotReturn = this.makeScreenshots(start, curr);
				if (!screenshotReturn) {
					return false;
				}
				max = curr + this.screenshotsPerInstance;
				start = curr + 1;
			}
			
			curr++;
			file = Paths.get(videoBaseDirectory + "/" + this.framesDirectory + "/" + this.framesPrefix + Integer.toString(curr) + ".html").toFile();
		}
		
		if(curr == start) {
			return true;
		}
		
		return this.makeScreenshots(start, curr-1);
	}
	
	private boolean makeScreenshots(int start, int end) {
		ProcessBuilder processBuilder;
		Process process;
		int processReturn;
		try {
			processBuilder = new ProcessBuilder();
			processBuilder.directory(new File("./" + videoBaseDirectory));
			processBuilder.command("./" + videoBaseDirectory + "/phantomjs.exe", "makeScreenshot.js", this.framesDirectory + "/" + this.framesPrefix, this.framesDirectory + "/" + this.framesPrefix, Integer.toString(start), Integer.toString(end));
			process = processBuilder.start();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			processReturn = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		if (processReturn != 0) {
			return false;
		}
		return true;
	}
	
	private boolean makeVideoFromScreenshots() {
		ProcessBuilder processBuilder;
		Process process;
		int processReturn;
		try {
			processBuilder = new ProcessBuilder();
			processBuilder.directory(new File("./" + videoBaseDirectory));
			
			List<String> commands = new ArrayList<>();
			
			commands.add("./" + videoBaseDirectory + "/ffmpeg.exe");
			
			if(this.overwriteOutput) {
				commands.add("-y");
			}
			
			commands.add("-framerate");
			commands.add(Integer.toString(this.framerate));
			commands.add("-start_number");
			commands.add("0");
			commands.add("-i");
			commands.add(this.framesDirectory + "/" + this.framesPrefix + "%d.jpeg");
			commands.add("-r");
			commands.add(Integer.toString(this.framerate));
			commands.add(this.outputFilePath + ".mp4");
			
			processBuilder.command(commands);
			
			process = processBuilder.start();
			
			InputStream err = process.getErrorStream();
			BufferedReader errBuf = new BufferedReader(new InputStreamReader(err));
			
			Runnable errStreamReader = () -> {
				String line;
				try {
					line = errBuf.readLine();
					while(line != null) {
						line = errBuf.readLine();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
			
			ExecutorService executor = Executors.newFixedThreadPool(10);
			executor.execute(errStreamReader);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			processReturn = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		if (processReturn != 0) {
			return false;
		}
		return true;
	}
}