var page = require("webpage").create();
var fs = require("fs");
var args = require("system").args;

var url = args[1];
var path = args[2];
var framerate = parseInt(args[3]);

var screenshotInterval = 1000/framerate;
var interval;
var frame;

page.viewportSize = { width: 1680, height: 970 };

page.open(url, function() {
	setTimeout(function() {
		frame = 0;
		var clipRect = page.evaluate(function(){
			return document.querySelector('#chart').getBoundingClientRect();
		});

		page.clipRect = {
			top:    clipRect.top,
			left:   clipRect.left,
			width:  clipRect.width,
			height: clipRect.height
		};
		
		saveHtml(frame);

		interval = setInterval(function() {
			frame++;
			saveHtml(frame);
		}, screenshotInterval);
		
		page.evaluate(function() {
			var element = document.querySelector('#start');
			
			// create a mouse click event
			var event = document.createEvent( 'MouseEvents' );
			event.initMouseEvent( 'click', true, true, window, 1, 0, 0 );
 
			// send click to element
			element.dispatchEvent( event );
		});
		
	}, 1000);
});

var saveHtml = function(frame) {
	var pagehtml = page.evaluate("function() {"+ 
		"return '<html><head>' + document.head.innerHTML + '</head>' + '<body>' + document.body.innerHTML + '</body></html>';" + 
	"}");
	
	pagehtml = pagehtml.replace(/<script([\S\s]*?)>([\S\s]*?)<\/script>/ig,"");
	
	fs.write(path + frame +'.html', pagehtml, 'w');
}

// When callback received, that chart is finished loading, clear the interval for saving html files
// Ffmpeg drops the last 2 frames -> create 2 more
// Create another 2 more for smoother end of video
page.onCallback = function(data) {
	if(data && data.command && (data.command === 'exit')) {
		clearInterval(interval);
		frame++;
		saveHtml(frame);
		frame++;
		saveHtml(frame);
		frame++;
		saveHtml(frame);
		frame++;
		saveHtml(frame);
		phantom.exit();
	}
}
