FROM php:5.6-cli
ADD swarm-client.jar /lib/
RUN apt-get -y update && apt-get -y install git openjdk-7-jre openjdk-7-jdk wget && apt-get clean
RUN curl -sS https://getcomposer.org/installer | php && mv composer.phar /usr/local/bin/composer
RUN wget https://phar.phpunit.de/phpunit-old.phar -O phpunit.phar && chmod +x phpunit.phar && mv phpunit.phar /usr/local/bin/phpunit
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080", "-labels", "php-slave",  "-executors", "1"]

