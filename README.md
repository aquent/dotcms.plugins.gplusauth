Google Plus Authentication
====================
This plugin allows you to use Google+ OAuth Authentication in dotCMS.  

Included is a Viewtool that generates the login link and a servlet to handle the redirect from G+.

Installation
------------
* Before starting with this plugin you should set up a Google Cloud App at [cloud.google.com](http://cloud.google.com)
* You will need to enable the Google+ API, Set up a Conset Screen, and Generate an OAuth Client ID
* Navigate to the dotCMS Dynamic plugins page: "System" > "Dynamic Plugins"
* Click on "Upload plugin" and select the .jar file located in the "build/libs/" folder
* Navigate to the Structures Page and Edit the Host Structure
* Add the following 2 fields:
> GPlus Client ID (text field with varname=gplusClientId)
> GPlus Client Secret (text field with varname=gplusClientSecret)
* Navigate the Hosts page: "System" > "Hosts"
* Edit your Default Host and set the values for the new fields from the values in your google cloud app

See [Addidional Documentation](docs) for a walkthrough from dotCMS Bootcamp 2014 for additional information.

Usage Example
-------------
See [Sample VTLs](docs/vtl) For an example Template VTL Code and for use of the viewtool to create a login link.

Building
--------
* Install Gradle (if not already installed)
* gradle jar 
