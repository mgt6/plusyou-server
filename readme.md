#+U Server
This project exposes a REST api used by the +U application.

Some features of the api

* register for volunteering opportunities
* post messages to facebook
* invite facebook friends
* check into volunteering opportunities
* consult achieved awards
* ...

##software installation
* java 6 (<http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-javase6-419409.html>)
* maven 3 (<http://maven.apache.org/download.html>)
* tomcat 6 (<http://tomcat.apache.org/download-60.cgi>)
* mysql 5 (<http://dev.mysql.com/downloads/mysql>)

##facebook setup
* create application (<http://developers.facebook.com/docs/appsonfacebook/tutorial/#create>)

##maven setup
add or modify profiles (settings.xml)

    <profile>
        <id>plusyou-default</id>

        <properties>
            <database.server.url>jdbc:mysql://localhost/XXX</database.server.url><!--url of server database-->
            <database.server.username>XXX</database.server.username><!--username of server database-->
        </properties>
    </profile>

    <profile>
        <id>plusyou-development</id>

        <activation>
            <property>
                <name>env</name>
                <value>dev</value>
            </property>
        </activation>

        <properties>
            <facebook.appId>XXX</facebook.appId><!--facebook application id-->
            <facebook.appAccessToken>XXX</facebook.appAccessToken><!--facebook application access token (http://developers.facebook.com/docs/authentication/applications)-->
            <database.server.password>XXX</database.server.password><!--password of server database-->

            <server.home>XXX</server.home><!--tomcat home folder-->
            <server.protocol>http</server.protocol><!--tomcat server protocol-->
            <server.host>localhost:8080</server.host><!--tomcat server host (with portnumber because protocol is http-->
        </properties>
    </profile>

    <profile>
        <id>plusyou-production</id>

        <activation>
            <property>
                <name>env</name>
                <value>prd</value>
            </property>
        </activation>

        <properties>
            <facebook.appId>XXX</facebook.appId><!--facebook application id-->
            <facebook.appAccessToken>XXX</facebook.appAccessToken><!--facebook application access token (http://developers.facebook.com/docs/authentication/applications)-->
            <database.server.password>XXX</database.server.password><!--password of server database-->

            <server.home>XXX</server.home><!--tomcat home folder-->
            <server.protocol>https</server.protocol><!--tomcat server protocol-->
            <server.host>XXX</server.host><!--tomcat server host (without portnumber because protocol is https-->
        </properties>
    </profile>

activate plusyou-default profile (settings.xml)

    <activeProfiles>
        <activeProfile>plusyou-default</activeProfile>
    </activeProfiles>

##tomcat setup
add database connection settings (context.xml)

    <Resource
        type="javax.sql.DataSource"
        name="plusyou-server-database"
        username="XXX"
        password="XXX"
        driverClassName="com.mysql.jdbc.Driver"
        url="jdbc:mysql://localhost/XXX"
        maxActive="35"
        maxIdle="2"
        maxWait="5000"
        removeAbandoned="true"
        removeAbandonedTimeout="60"
        testOnBorrow="true"
        validationQuery="SELECT 1"/>

##mysql setup
run maven commando

    mvn -Denv=dev clean test

execute sql file target/test-classes/dbmaintain.sql as root user in mysql

run maven commando

    mvn -Denv=dev org.dbmaintain:dbmaintain-maven-plugin:2.4:updateDatabase

##useful maven commando's
create war file

    mvn -Denv=dev clean package

create war file and deploy to tomcat

    mvn -Denv=dev clean install

update database with sqls from src/main/config/database/scripts

    mvn -Denv=dev org.dbmaintain:dbmaintain-maven-plugin:2.4:updateDatabase