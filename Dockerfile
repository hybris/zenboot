FROM  base

RUN apt-get update
RUN apt-get install -y openjdk-7-jre-headless less wget curl
RUN apt-get clean

RUN echo " deb http://security.ubuntu.com/ubuntu precise-security main universe" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get -y install tomcat7
EXPOSE 8080
ADD target/zenboot-0.7.2.war /var/lib/tomcat7/webapps/zenboot.war
ADD zenboot.properties /usr/share/tomcat7/lib/zenboot.properties
CMD service tomcat7 start && tail -f /var/lib/tomcat7/logs/catalina.out
