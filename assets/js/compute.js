var body = document.getElementsByTagName('body')[0];
var desiredHeight = window.innerHeight - 50;
var desiredWidth = window.innerWidth - 30;

var totalHeight = body.offsetHeight;
var pageCount = Math.floor(totalHeight/desiredHeight) + 1;

manager.addPages(pageCount);
manager.finish();