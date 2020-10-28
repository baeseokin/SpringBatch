echo off
set arg1=%1
set arg2=%2
set arg3=%3
java -jar -Dspring.profiles.active=mysql ./target/spring-batch-example3-0.0.1-SNAPSHOT.jar --job.name=%arg1% version=%arg2% createDate=%arg3%