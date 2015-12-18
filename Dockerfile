FROM codenvy/jdk7_tomcat7
# https://github.com/codenvy/dockerfiles/blob/master/base/jdk7/Dockerfile
# FROM codenvy/shellinabox
# https://github.com/codenvy/dockerfiles/blob/master/base/shellinabox/Dockerfile
# FROM debian:jessie
# https://github.com/tianon/docker-brew-debian/blob/8105df0412f86c08d11e86f7f6bab6160ff1e837/jessie/Dockerfile
# FROM scratch
ADD zenboot.properties /etc/zenboot/zenboot.properties
USER root
RUN sudo apt-get update && sudo apt-get install -y curl ansible openssh-client sshpass socat dnsutils jq less vim netcat-openbsd git
RUN sudo ansible-galaxy install franklinkim.nginx
RUN cd /etc/ansible/roles && sudo git clone https://github.com/jivesoftware/ansible-consul.git savagegus.consul && cd savagegus.consul && git checkout 0ecd1bc460a2ad4b942a8e6035a8412671e7b6c9 
ADD docker-provisioning/ansible.cfg /etc/ansible/ansible.cfg
USER user
RUN mkdir -p /home/user/zenboot
ADD https://github.com/hybris/zenboot/releases/download/v0.9.18/zenboot.war /home/user/tomcat7/webapps/zenboot.war
ADD docker-provisioning/setenv.sh /home/user/tomcat7/bin/setenv.sh
RUN sudo chown user:user /home/user/tomcat7/bin/setenv.sh
#ADD target/zenboot.war /home/user/tomcat7/webapps/zenboot.war
RUN sudo chown user:user /home/user/tomcat7/webapps/zenboot.war

EXPOSE 8080
