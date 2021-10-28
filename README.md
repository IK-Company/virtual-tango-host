# virtual Tango host

### Usage

1. Start a new project

2. Add dependency to you pom.xml or whatever
   
   ```xml
   <dependency>
     <groupId>com.ik-company.tango</groupId>
     <artifactId>virtual-tango-host</artifactId>
     <version>0.2</version>
   </dependency>
   ```

   The above requires GitHub packages authorization in your settings.xml:

   ```xml
   <servers>
        <server>
          <id>github</id>
          <username>YOUR_GITHUB_USER</username>
          <password>YOUR_GITHUB_TOKEN</password>
        </server>
   </servers>
   <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>github-ik</id>
            <url>https://maven.pkg.github.com/ik-company/*</url>
        </repository>
        <!-- this one is for JTango -->
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>github-hzg</id>
            <url>https://maven.pkg.github.com/hereon-wpi/*</url>
        </repository>
    </repositories>
   ```

3. Implement DbBackend

   ```java
   public class MyDbBackend implements DbBackend{
       //TODO   
   }
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