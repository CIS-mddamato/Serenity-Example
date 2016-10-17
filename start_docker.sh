
#!/bin/bash 
JAVA_HOME=/usr/java/jdk1.8.0_51
PATH=$PATH:/opt/gradle/gradle-2.6:$JAVA_HOME/bin:/opt/gradle/gradle-2.6/bin:
GRADLE_HOME=/opt/gradle/gradle-2.6
export JAVA_HOME PATH GRADLE_HOME

export SCREEN_WIDTH=1920 
export SCREEN_HEIGHT=1080 
export SCREEN_DEPTH=24

docker run -d -p 4444:4444 -e JAVA_OPTS=-Xmx2048m b5be2c7f3716
if [ $? -ne 0 ]; then
docker stop `docker ps -qa `
docker rm `docker ps -qa`

sleep 3
docker run -d -p 4444:4444 -e JAVA_OPTS=-Xmx2048m  b5be2c7f3716
else
echo " nothing is running"
fi
sleep 3
docker ps

#cd $WORKSPACE
pwd
sleep 10


gradle clean test -Papp.base.host=10.103.138.59 -Ptags=API -DstoryFilter=do_not_run -Pwebdriver.driver=remote -Pwebdriver.remote.driver="chrome" -Pwebdriver.remote.url="http://localhost:4444/wd/hub" --info

chmod -R  go+r target/site/serenity/*

rm -fr /var/www/html/serenity/*

cp -pR target/site/serenity/* /var/www/html/serenity/
