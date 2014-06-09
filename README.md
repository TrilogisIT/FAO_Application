FAO Application (Android and Desktop)
===============

Desktop and Android application for FAO Locust Watch project and Europa Challenge 2014.

This application has been donated to help FAO on the Locust Watch project (http://www.fao.org/ag/locusts/en/info/info/index.html).

The goal of this application is to help FAO at providing support to national locust operators in Africa and Middle East.
Desert locusts are a huge problem for the population and due to their ability to change their behaviours and habits.
These locusts are hard to limit as they form swarms and move rapidly (about 20km/h). Moreover, they can consume (in 1km² swarm) as much food as 35.000 people eats in a single day.


How to compile/run the code
-------

Android Application:
* Download the Android WorldWind SDK at: [GitHub Repo](https://github.com/TrilogisIT/WorldWind_Android/tree/fao-master) and import in eclipse (fix your android sdk dependencies, download needed api versions)
* Download the Android_app and import in eclipse. Fix library dependency using the SDK imported above.
* Run the application as standard Android application, but remember to copy your zip data before doing this (the application does not try to download tiles from the web!) [Sample Data](http://goo.gl/lvwYdY)


Desktop Application:
The desktop application can't compile as it is because we did not want to put ALL the WorldWind SDK sources to both avoid code replication and avoid this repo to be hundreds of megabytes. All the needed sources are free and downloadable from NASA website.
* Download both the WorldWindTileCreator and WorldWind_Project data
* Download standard WorldWind desktop SDK [WorldWind SDK](http://builds.worldwind.arc.nasa.gov/download-release-1.5.1.asp) . We used WW 1.5.1, 2.0 has not tested and could be lead to a non working app.
* Put the whole WW SDK into WorldWind_Project, but do not overwrite java sources (or you can put WorldWind_Project sources over the WW sdk sources.
* Import WorldWindTileCreator and make it dependant to WorldWind_Project created above.
* Run as a standard Swing application (GUIClass is the main class), import your TIFF and DEM data, set folders, project name and coordinates, and run the Tiling Work!
* If the app fails to show TIFF when "Browsing" for files, close and reopen the Browsing window, this is due to the GDAL libraries that sometimes are loaded later than the Browsing window.


Credits
-------

Author: Nicola Dorigatti and Nicola Meneghini([Trilogis Srl](http://www.trilogis.it))

* WorldWind is an opensource API relased by [NASA](http://www.nasa.gov/) under the [NASA Open Source Agreement (NOSA)](http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html)

![Screen](http://www.trilogis.it/wp-content/uploads/2013/07/logo_ufficiale-e1375429066884.png)
![Screen](http://www.nasa.gov/sites/all/themes/custom/NASAOmegaHTML5/images/nasa-logo.png)
![Screen](http://www.fao.org/fileadmin/templates/faoweb/images/FAO-logo.png)

Further information and screenshots can be found at [our website](http://www.trilogis.it/eLocust3D)

NASA and the NASA logo are registered trademarks of NASA, used with permission.
FAO and the FAO logo are registered trademarks of FAO, used with permission.


License
--------

    Copyright 2014 Trilogis SRL

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
