The Thing Browser is a framework for loosely coupled components -- eventually, distributed objects -- that can be intuitively and securely composed by end-users. The project covers:

  * An argument for object-oriented user interfaces to software components (and the corresponding end-user information model);
  * A way for components to be accessible using the familiar Web browser user interface; and
  * A plan to interoperate with "plain" HTTP resources, including HTML, both programmatically and in the user interface.

Between them, these concepts have precedents in [Squeak](http://squeak.org/) and other Smalltalks; [Self](http://research.sun.com/self/); [OpenDoc](http://en.wikipedia.org/wiki/OpenDoc); [Cyberdog](http://cyberdog.org/); the [OS/2](http://en.wikipedia.org/wiki/OS/2)
[Workplace Shell](http://en.wikipedia.org/wiki/Workplace_Shell); and [Internet Explorer](http://www.microsoft.com/windows/products/winfamily/ie/default.mspx) extensions using [COM components](http://www.microsoft.com/com/). It is also similar to a project called [FavaBeans](http://favabeans.sourceforge.net/) developed by Ihab Awad and the [NakedObjects](http://www.nakedobjects.org/) platform.

To the extent that casual composition of components from the Internet is hampered mainly by security concerns, and composition by direct manipulation (e.g., drag and drop) maps very well to capability security, the Thing Browser project is largely about security. The Open Source Google Caja project intends to bring capability security to JavaScript, and we would like to start thinking about suitable UI models.

The Thing Browser work, taken to its logical conclusion, would either adopt or invent a highly improved component model for the Internet. Since we hope to have buy-in from multiple vendors on such a model, it makes sense to do our work in the open.

Eventually, the Thing Browser project will rely on
[Google Gears](http://code.google.com/p/google-gears) and
[Google Caja](http://code.google.com/p/google-caja).