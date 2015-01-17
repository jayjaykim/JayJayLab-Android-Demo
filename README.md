JayJayLab-Android-Demo
======================

This simple Android app demonstrates how JayJayLab implements Java Android application and what kinds of open sources we use
for even simple function. Our philosophy of application, system development is 
first, use the latest and robust software technology to satisfy requirements
Secondly, use open source as many as possible.

For the time being, there's only one sub application which is called "GreyHound" recording the current location in background.
Our app is always based on some kinds of DI framework like RoboGuice, Dagger or something. The demo app is built on top of Roboguice.

Introduction to each part of the application

Main Screen
As you may know, demo app is made of latest Android support libraries such as Toolbar, RecyclerView, CardView and so on.
And every bitmap images are passed through Picasso library, thanks Square :). Actually, I(jayjay) used to use UIL(Universial Image
 Loader) but it turns out to be many developers prefer Picasso for easy of use, popoularity and in trust of Square ability.

GreyHound
This sub application records locations in gpx file format and plot it on Google Map. Until the app isn't deleted, gpx files
are stored historically and you can see a list of gpx files sorted by generated time. For time manipulation, Joda time is used
by external libary which is now default in Java 8. And to manage Persistence GreenDAO is used to store gpx files and query on them.

