mvn clean compile package -DskipTests
scp fk-cs-languagetool-service/target/fk-cs-languagetool-service-3.6.jar 10.85.123.228:/home/anmol.kapoor/
scp /Users/anmol.kapoor/repos/spellcheckWork/languagetool/fk-cs-languagetool-service/src/main/resources/config/nm/configuration.yaml 10.85.123.228:/home/anmol.kapoor
