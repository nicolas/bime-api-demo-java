A sample application that shows integration with the Bime API. Built with Java [PlayFramework](http://www.playframework.com/)

It is not by any mean a best practice guide or production code quality. It's intend is purely to demo how the API can be used to integrate and drive Bime's dashboards from a third party application.

It shows how to authenticate and then make a request to Bime API using [Scribe](https://github.com/fernandezpablo85/scribe-java).

The two important classes are :

* app/api/BimeApi which extends DefaultApi20 and where you have to replace matthieu by your own account name
* app/controllers/Bime which shows the flow (starting with connect) and where you have to replace the keys by yours

For more information on possible actions see [the documentation](https://github.com/nicolas/bime-api).