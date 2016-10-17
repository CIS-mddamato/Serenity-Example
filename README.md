# NASS/Infopass Test Automation  

This is the repository for the portion of test automation that covers functional UI and API tests.  


## Tools
NASS/Infopass utilizes and requires the following tools:  
* Gradle Wrapper 2.12
* Java 8
* Cucumber
* Firefox/Chrome (Firefox version 38.2.0) for running automated tests
  * If using Chrome you will need chromedriver.exe+
* Github Enterprise - version control
* Serenity-BDD for test running and reporting
* API Tests
* UI Tests
  * story-map-library-page class structure

## Configuration
### serenity.properties
Serenity is managed by the `serenity.properties` file. This is where most of the test settings are located and where dynamic variables that relate to the tests actually running are located.  
When the project is built with gradle a number of these properties can be set using command line arguments or just modifying the `serenity.properties` file directly. To pass the settings via command line send `-PpropertyName= ` as an argument.
The properties that can be passed are as follows:
```
app.base.protocol
app.base.host
app.base.port
app.base.basePath
app.base.loginPath
metafilter
tags
```
Examples and explanations of these:  
`-Papp.base.protocol=http` Set the protocol  
`-Papp.base.host=10.103.138.59` The IP address of the server you want to test  
`-Papp.base.port=8080` The port required by the test server  
`-Papp.base.basePath=/infopass-admin` the path to the main location for API tests to begin  
`-Papp.base.loginPath=/infopass-admin/login` this is the path required for the UI tests to log into the application  
`-Pmetafilter=DEMO` text  
`=Ptags=API` text  


 * -Punattended=Y - overwrites the properties below to default values specificied in gradle.properties. 
 * If these need to be specified individulally do not use -Punattended
 * 
 * -Pwebdriver.remote.driver=
 * -Pwebdriver.remote.url=
 * -Pwebdriver.driver=
 * -Pwebdriver.firefox.profile=

firefox profile location is set with:
```
webdriver.firefox.profile=
```
Stories and JUnit tests can be filters by tags in this file using:
```
metafilter=
tags=
```
The webdriver implicit timeout option can be set with:
```
webdriver.timeouts.implicitlywait=10000
```
Screenshots can be taken at set intervals. The options are  `FOR_FAILURES`, `BEFORE_AND_AFTER_EACH_STEP`,`FOR_EACH_ACTION` and can be set with:
```
serenity.take.screenshots=FOR_FAILURES
```
### build.gradle and gradle.properties
 * in progress...



## Running the tests
The entire suite can be run with the current settings that are set in the `serenity.properties` file by running
```
gradle clean test aggregate
```
from the project directory.  
However many parameters can be passed in to limit the amount of tests running at one time.  
To filter the tests to run one single story/feature at a time leaving all other settings intact:
```
gradle clean test aggregate -DstoryFilter=storyName
```

Any of the Environment Information data listed above can be updated from the command line. Running this command will update the serenity.properties file:
```
-Papp.base.host=10.103.138.59
```



//this is not finished
This to test github-webhooks
