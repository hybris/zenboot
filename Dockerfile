FROM codenvy/jdk7_tomcat7
ADD zenboot.properties /etc/zenboot/zenboot.properties
RUN sudo apt-get update && sudo apt-get install -y curl ansible openssh-client sshpass socat dnsutils jq
ADD docker-provisioning/ansible.cfg /etc/ansible/ansible.cfg
RUN mkdir -p /home/user/zenboot
ADD https://github.com/hybris/zenboot/releases/download/v0.7.25/zenboot.war /home/user/tomcat7/webapps/zenboot.war
#ADD target/zenboot.war /home/user/tomcat7/webapps/zenboot.war
RUN sudo chown user:user /home/user/tomcat7/webapps/zenboot.war
ADD docker-provisioning/boot.sh /home/user/tomcat7/bin/boot.sh
RUN sudo chown user:user /home/user/tomcat7/bin/boot.sh && sudo chmod u+x /home/user/tomcat7/bin/boot.sh 

WORKDIR /home/user/tomcat7/bin

CMD ./boot.sh

EXPOSE 8080
