var body = document.getElementsByTagName('body')[0];
var desiredHeight = window.innerHeight - 50;
var desiredWidth = window.innerWidth - 50;

totalHeight = body.offsetHeight;
pageCount = Math.floor(totalHeight/desiredHeight) + 1;

body.style.display = "block";
body.style.margin = "0px";
body.style.padding = "20px";
body.style.lineHeight = "60px";
/*
body.style.width = desiredWidth * pageCount + "px";
body.style.height = desiredHeight + "px";

body.style.WebkitColumnCount = pageCount;
body.style.WebkitColumnWidth = desiredWidth + "px";
body.style.WebkitColumnGap = "20px";


document.body.addEventListener('touchmove', function(e) {
	  e.preventDefault();
}, false);*/

manager.log(desiredWidth + " " + desiredHeight);
window.scrollBy((desiredWidth + 10)*manager.getPage(),0);