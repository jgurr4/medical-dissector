FROM adoptopenjdk/openjdk16:alpine-jre
COPY build/libs/springPractice-0.0.1-SNAPSHOT-fat.jar /
WORKDIR /
CMD java -cp springPractice-0.0.1-SNAPSHOT-fat.jar com.wild.medicalTermDissector.MedTermDissector
