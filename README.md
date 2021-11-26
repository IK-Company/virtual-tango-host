# virtual Tango host

[![wakatime](https://wakatime.com/badge/user/87464ce7-a479-47b1-b3aa-2548252894d7/project/2c6b26d3-3de6-48fe-ad99-a50f734b7750.svg)](https://wakatime.com/badge/user/87464ce7-a479-47b1-b3aa-2548252894d7/project/2c6b26d3-3de6-48fe-ad99-a50f734b7750)

### Usage

1. Start a new project

2. Add dependency to you pom.xml or whatever
   
   ```xml
   <dependency>
     <groupId>waltz-controls.tango</groupId>
     <artifactId>virtual-tango-host</artifactId>
     <version>0.2</version>
   </dependency>
   ```

   The above requires GitHub packages authorization in your settings.xml:

   ```xml
   <servers>
        <server>
          <id>github-waltz</id>
          <username>YOUR_GITHUB_USER</username>
          <password>YOUR_GITHUB_TOKEN</password>
        </server>
   </servers>
   <repositories>
        <!-- this one is for JTango -->
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>github-waltz</id>
            <url>https://maven.pkg.github.com/waltz-controls/*</url>
        </repository>
    </repositories>
   ```

3. [Optional] Implement DbBackend

   ```java
   public class MyDbBackend implements DbBackend{
       //TODO   
   }
   ```
   
   To use simple in-memory backend just provide a .properties file with Tango devices (`-DentriesFile=<path-to-file>`):

   ```
   virtual/test/0=TestServer.class
   virtual/test/1=TestServer.class
   #etc
   ```

4. Register your DbBackend in the DbBackendFactory and start the virtual Tango db

   ```java
    public class Main {
     public static void main(String[] args){
       DbBackendFactory.getInstance().setDbBackendImplementationClass(MyDbBackend.class);
       new DataBase.Launcher().run();
     }
   }
   ```