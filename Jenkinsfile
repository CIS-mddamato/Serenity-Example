properties([
    parameters(
    		[
    		 string(defaultValue: 'git@git.uscis.dhs.gov:USCIS/nass-test.git', 
    				description: 'Test code repo URL', 
    				name: 'TEST_CODE_REPO_URL'),
    		 string(defaultValue: 'master', 
    	    		description: 'Test code Branch', 
    	    		name: 'TEST_CODE_REPO_BRANCH'),
    		 string(defaultValue: 'git@git.uscis.dhs.gov:USCIS/NASS-FieldOps.git', 
     				description: 'Application Code Repo URL', 
     				name: 'APP_CODE_REPO_URL'),
    		 string(defaultValue: 'master', 
     				description: 'Application Code Repo Branch', 
     				name: 'APP_CODE_REPO_BRANCH'),
    		 choice(choices: 'jdbc:oracle:thin:@localhost:1521:XE\njdbc:oracle:thin:@nass-dvlpmnt-idb12.cpriodarchzx.us-east-1.rds.amazonaws.com:1525:NASSDEV2', 
    				 description: 'Oracle URL', 
    				 name: 'ORACLE_URL'),
    		 choice(choices: 'local\ndev1', 
    				 description: 'which property folder to use?', 
    				 name: 'PROPERTY_SET'),
    		 booleanParam(defaultValue: true, 
    				 description: 'Should the 44 machine shut down after the tests have run? (Conserve $$)', 
    				 name: 'SHUTDOWN_SIGNAL'),
    		 string(defaultValue: '--tags @API,@FunctionalTests', 
      				description: 'Cucumber Option', 
      				name: 'CUCUMBER_OPTIONS'),
    		 ]),
    disableConcurrentBuilds(),
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '30'))
])
	




try{
	node ('master'){
	
    stage('Start 44 Node'){
    try {
      
        sh """
          ping -c3 10.103.138.44;
        """
        echo 'The machine is already online.'
    } catch (Exception e) {
        echo 'The machine is off.. Starting now..'
        checkout([
            $class: 'GitSCM',
            branches: [[name: '*/master']],
            doGenerateSubmoduleConfigurations: false,
            extensions: [[
                $class: 'RelativeTargetDirectory',
                relativeTargetDir: 'AnsibleScripts']],
            submoduleCfg: [],
                userRemoteConfigs: [[
                    credentialsId: 'ec40243f-9b52-4346-a7ca-f42be39f1121',
                    url: 'git@gist.git.uscis.dhs.gov:f082fb0c3559bb0c8f3c182042dbc718.git']]
        ])
        dir('AnsibleScripts'){
            sh '''
                #!/bin/bash
                ansible-playbook -v start_44_ansible.yml --vault-password-file /var/lib/jenkins/ansible_files/.passForAnsibleEnc;

                echo "waiting 60 seconds for EC2 to boot up";
                for i in {30..1};
                    do echo "Counting down: $i ";
                    sleep 2;
                done;
        
                echo 'OK..';
                echo "Ping slave to see if online";
                ping -c15 10.103.138.44;
            '''
        }
      }
    }
  }

  
  
  node('Node-44'){
      def mvnHome = tool 'Maven 3.3.9';

    stage ('remove old database'){
    	
    	if("${ORACLE_URL}".toLowerCase().contains("localhost")){
    	    sh """
    	      sqlplus system/NassFodLocaldb<<EOF
    	      spool output1
    	      DROP USER INFPDATA CASCADE;
    	      commit; 
    	      CREATE USER INFPDATA IDENTIFIED BY l1QU1B4S3 DEFAULT TABLESPACE INFP_DATA QUOTA UNLIMITED ON INFP_DATA TEMPORARY TABLESPACE temp QUOTA 5M ON system; 
    	      alter USER INFPDATA QUOTA UNLIMITED ON INFP_INDEX; 
    	      ALTER USER INFPDATA QUOTA 100M ON INFP_LOB;
    	      GRANT CREATE SESSION TO INFPDATA; 
    	      GRANT CREATE TABLE TO INFPDATA; 
    	      GRANT CREATE SEQUENCE TO INFPDATA; 
    	      GRANT CREATE PUBLIC SYNONYM to INFPDATA; 
    	      commit; 
    	      disconnect;
    	      spool off
    	      exit;
    	      EOF
    	    
    	    """
    	}
    	else{
    		
    	}

    }

    stage ('Pull NASS-Fieldops Code'){
      dir("${WORKSPACE}"){
        checkout([
            $class: 'GitSCM',
            branches: [[name: '*/${APP_CODE_REPO_BRANCH}']],
            doGenerateSubmoduleConfigurations: false,
            extensions: [[
                $class: 'RelativeTargetDirectory',
                relativeTargetDir: 'NassInfoPassApplicationCode']],
            submoduleCfg: [],
                userRemoteConfigs: [[
                    credentialsId: 'ec40243f-9b52-4346-a7ca-f42be39f1121',
                    url: '${APP_CODE_REPO_URL}']]
        ])
      }
    }

    stage('Build New Database'){
	  if("${ORACLE_URL}".toLowerCase().contains("localhost")){
		  echo "Building localhost database"
		  dir('NassInfoPassApplicationCode/nass-liquibase/'){
			  sh """
			  	${mvnHome}/bin/mvn clean -Dliquibase.url="${ORACLE_URL}" install;
			  """
		  }
	  }
	  else
	  {
		  echo "Using External Database"
	  }

  }
  
  
  stage('Build Application'){
	  
	  if("${ORACLE_URL}".toLowerCase().contains("localhost")){
		  echo "Starting application using local database"
		  
		  dir('NassInfoPassApplicationCode/'){
			  sh """
			  sudo rm -rf /config/*
			  	${mvnHome}/bin/mvn clean install
			  """
			  //*/
		  }
		  dir('NassInfoPassApplicationCode/nass-fieldops-parent/'){
		      sh """
		      	sudo service tomcat stop
				${mvnHome}/bin/mvn clean -DskipTests -Dliquibase.url="${ORACLE_URL}" install;
				sudo rm -rf /usr/local/tomcat7/webapps/infopass-admin
				sudo rm -rf /usr/local/tomcat7/webapps/infopass-admin.war
				scp ${WORKSPACE}/NassInfoPassApplicationCode/nass-fieldops-admin/target/infopass-admin.war tomcat@localhost:/usr/local/tomcat7/webapps/infopass-admin.war
				
				# COPY OVER THE CORRECT PROPERTIES FILES
				sudo rm -rf /config/*
			  	cp ${WORKSPACE}/NassInfoPassApplicationCode/nass-config/${PROPERTY_SET}/* /config/
				sudo chown -R tomcat:tomcat /config/*
				
				sudo service tomcat start
				"""
				sh """ echo "waiting 60 seconds for Tomcat to reboot"; """
				sh ''' for i in {30..1}; do echo "Counting down: $i "; sleep 2; done; echo 'OK..'; '''
				
				//*/
		  }
	  }
	  else
	  {
		  echo "Starting application using external database"
		  dir('NassInfoPassApplicationCode/'){
			  sh """
			  sudo rm -rf /config/*
			  	${mvnHome}/bin/mvn clean install
			  """
			  //*/
		  }
		  
		  dir('NassInfoPassApplicationCode/nass-fieldops-parent/'){
			  sh """
		      	sudo service tomcat stop
				${mvnHome}/bin/mvn clean -DskipTests -Dliquibase.url="${ORACLE_URL}" -Dliquibase.username="INFPDATA" -Dliquibase.password="N400_temp" -Dliquibase.promptOnNonLocalDatabase="false" install;
				sudo rm -rf /usr/local/tomcat7/webapps/infopass-admin
				sudo rm -rf /usr/local/tomcat7/webapps/infopass-admin.war
				scp ${WORKSPACE}/NassInfoPassApplicationCode/nass-fieldops-admin/target/infopass-admin.war tomcat@localhost:/usr/local/tomcat7/webapps/infopass-admin.war
			"""
				
				/// COPY OVER THE CORRECT PROPERTIES FILES
				sh """
					sudo rm -rf /config/*
			  		cp ${WORKSPACE}/NassInfoPassApplicationCode/nass-config/${PROPERTY_SET}/* /config/
					sudo chown -R tomcat:tomcat /config/*
				"""
				//*/
					
				sh """sudo service tomcat start"""
				sh """ echo "waiting 60 seconds for Tomcat to reboot"; """
				sh ''' for i in {30..1}; do echo "Counting down: $i "; sleep 2; done; echo 'OK..'; '''
				
		  }
		  
	  }


  }
  
  stage('Pull NASS Test Code'){
      checkout([
                $class: 'GitSCM',
                branches: [[name: '*/${TEST_CODE_REPO_BRANCH}']],
                doGenerateSubmoduleConfigurations: false,
                extensions: [[
                    $class: 'RelativeTargetDirectory',
                    relativeTargetDir: 'NassTestCode']],
                submoduleCfg: [],
                    userRemoteConfigs: [[
                        credentialsId: 'ec40243f-9b52-4346-a7ca-f42be39f1121',
                        url: '${TEST_CODE_REPO_URL}']]
            ])
  }
  
  
  stage('Start Selenium GRID'){
	  
	  //Stop the docker containers in case some stayed on for some reason..
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=selenium-hub-info-suite"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-01"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-02"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-03"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-04"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-05"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-06"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-07"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-08"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-09"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-10"))'''} catch(Exception e){}
		try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-11"))'''} catch(Exception e){}
	
		// Start the selenium hub and its children
		sh """
		  docker run -d -p 4444:4444 -v /dev/shm:/dev/shm -e JAVA_OPTS=-Xmx8192m -e GRID_BROWSER_TIMEOUT=30000 -e GRID_MAX_SESSION=10 --name selenium-hub-info-suite 81687cf826f679fc3b94d61684b84422531d56e3877ff585cf4b504b258aef37
		  #Start chrome nodes
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-01 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-02 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-03 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-04 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-05 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-06 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-07 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-08 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-09 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-10 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
		  docker run -d --link selenium-hub-info-suite:hub -v /dev/shm:/dev/shm --name info-suite-node-11 3fd3fdb00c6048bf8cf705e05a484672187c69b0d5e407df6ca8a343ae2212db
	  """

	  
  }
  
  stage('Run Automation Tests'){
	 

	  dir("${WORKSPACE}/NassTestCode"){
		  sh "chmod u+x gradlew"
		  sh "docker ps -a"
		  sh "sleep 3"
		  
		  sh "./gradlew --version"
		  
		  sh """
		  		./gradlew nass-infopass-test:clean nass-infopass-test:test nass-infopass-test:aggregate -Dcucumber.options="${CUCUMBER_OPTIONS}" -Pwebdriver.driver=remote -Pwebdriver.remote.driver="chrome" -Pwebdriver.remote.url="http://localhost:4444/wd/hub" -PmaxParallelForks=10 -Papp.base.host=localhost -Papp.base.port=8080 -Papp.base.basePath=/infopass-admin -Papp.base.loginPath=/infopass-admin/login -Papp.base.protocol=http
		  """
		  	
		  step([$class: 'JUnitResultArchiver', testResults: 'nass-infopass-test/build/test-results/*.xml'])
	  }
		  
	
		  //Invoke Gradle Tasks: nass-infopass-test:clean nass-infopass-test:test nass-infopass-test:aggregate
		  //
		  
	  }
  }
  
}

catch(Exception e) {
	//RUN THIS SECTION WHEN AN ERROR IN THE BUILD OCCURS
	
}
finally{
	//RUN THIS SECTION OF CODE REGARDLESS OF BUILD OUTCOME
	node ('Node-44'){
		stage('Perform Post-Build Actions'){
			
			//stop all the docker images and clean up
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=selenium-hub-info-suite"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-01"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-02"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-03"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-04"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-05"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-06"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-07"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-08"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-09"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-10"))'''} catch(Exception e){}
			try {sh '''docker rm $(docker stop $(docker ps -a -q --filter="name=info-suite-node-11"))'''} catch(Exception e){}
			
			//Not done yet..
			
			//#Copy reports from .44 machine to .43
			//scp -pr /var/lib/jenkins/workspace/${JOB_NAME}/nass-infopass-test/target/site/serenity jenkins@10.103.138.43:/var/lib/jenkins/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}/archive;
			//#SSH into .43 and create symlink to the latest build number
			//ssh 10.103.138.43 "cd /var/lib/jenkins/jobs/${JOB_NAME}/builds; rm -f latest; ln -s ${BUILD_NUMBER} latest; chmod -R 755 /var/lib/jenkins/jobs  "
			
			//GROOVY POSTBUILD PLUGIN:
			//reportUrl = "http://10.103.138.43:8088/jobs/"+manager.getEnvVariable("JOB_NAME")+"/builds/"+manager.getEnvVariable("BUILD_NUMBER")+"/archive/";
			//manager.addBadge("folder.gif", "View Report", reportUrl);
			
			
			//Publish JUnit test result report
			
			//Fail build is regex: (<test-step result=\"FAILURE\") is found inside nass-infopass-test/target/site/serenity/*.xml
		}
	}
	
	node ('master'){
	    
		stage('Shut Down 44 Node'){
	    	echo "The SHUTDOWN_SIGNAL is ${SHUTDOWN_SIGNAL}"
	    	if("${SHUTDOWN_SIGNAL}".contains("true")){
	    		echo "Shutting the node down"
	    		dir('${WORKSPACE}'){
	    			checkout([$class: 'GitSCM',
	    		          branches: [[name: '*/master']],
	    		          doGenerateSubmoduleConfigurations: false,
	    		          extensions: [[
	    		          $class: 'RelativeTargetDirectory',
	    		          relativeTargetDir: 'AnsibleScripts']],
	    		          submoduleCfg: [],
	    		          userRemoteConfigs: [[
	    		          credentialsId: 'ec40243f-9b52-4346-a7ca-f42be39f1121',
	    		          url: 'git@gist.git.uscis.dhs.gov:f082fb0c3559bb0c8f3c182042dbc718.git']]
	    		     ])
	    		}
		        dir('${WORKSPACE}/AnsibleScripts'){
		            sh '''
		                echo "ansible-playbook -v stop_44_ansible.yml --vault-password-file /var/lib/jenkins/ansible_files/.passForAnsibleEnc;" | at now;
		            '''
		        }
	    	}else{
	    		echo "The node will not be shut down"
	    	}
	    }
	    
	    
	    
	}
	
	

	
	
}
