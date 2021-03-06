var page = require("webpage").create();
var fs = require("fs");
var args = require("system").args;

var directoryRelative = args[1]
var outputDirectoryRelative = args[2];
var start = parseInt(args[3]);
var end = parseInt(args[4]);


page.viewportSize = { width: 1680, height: 970 };


var takeScreenshots = function(curr) {
	return function(status) {
		setTimeout(function() {
			var clipRect = page.evaluate(function(){
				return document.querySelector('#chart').getBoundingClientRect();
			});

			page.clipRect = {
				top:    clipRect.top,
				left:   clipRect.left,
				width:  clipRect.width,
				height: clipRect.height
			};

			page.render(outputDirectoryRelative + curr + ".jpeg", { format: "jpeg" });
			
			var next = curr + 1;
			
			if(next > end) {
				// page.render is non-blocking when saving file. Avoid exit before last file is written by checking for existence
				setInterval(function(){
				  if(fs.isFile(outputDirectoryRelative + curr + ".jpeg")){
					  phantom.exit();
				  }
				}, 100);
			}
			else {
				var nextPathRelative = directoryRelative + next + ".html";
				var nextPathAbsolute = "file:///" + fs.absolute(nextPathRelative);
			
				page.open(nextPathAbsolute, takeScreenshots(next));
			}
		}, 5);
	}
}

var firstPathRelative = directoryRelative + start + ".html";
var firstPathAbsolute = "file:///" + fs.absolute(firstPathRelative);

page.open(firstPathAbsolute, takeScreenshots(start));