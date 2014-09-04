FROM codenvy/jdk7_tomcat7
ADD target/zenboot-0.7.2.war /home/user/tomcat7/webapps/zenboot.war
ADD zenboot.properties /etc/zenboot/zenboot.properties
RUN sudo apt-get update && sudo apt-get install -y curl ansible openssh-client sshpass socat dnsutils
RUN mkdir -p /home/user/zenboot

EXPOSE 8080
