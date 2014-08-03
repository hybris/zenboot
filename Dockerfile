FROM codenvy/jdk7_tomcat7
ADD target/zenboot-0.7.2.war /home/user/tomcat7/webapps/zenboot.war
ADD zenboot.properties /etc/zenboot/zenboot.properties
RUN mkdir -p /home/user/zenboot
