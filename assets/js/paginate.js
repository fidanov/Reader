var body = document.getElementsByTagName('body')[0];
var desiredHeight = window.innerHeight - 50;
var desiredWidth = window.innerWidth - 30;

totalHeight = body.offsetHeight;
pageCount = Math.floor(totalHeight/desiredHeight) + 1;

body.style.padding = "10px";
body.style.width = desiredWidth * pageCount + "px";
body.style.height = desiredHeight + "px";
body.style.WebkitColumnCount = pageCount;
body.style.WebkitColumnWidth = desiredWidth + "px";
body.style.WebkitColumnGap = "10px";

document.body.addEventListener('touchmove', function(e) {
	  e.preventDefault();
}, false);

window.scrollBy((desiredWidth + 10)*manager.getPage(),0);