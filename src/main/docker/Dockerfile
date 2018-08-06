FROM dvno/ubuntujava8
VOLUME /tmp
ADD toktalk-0.0.1-SNAPSHOT.jar /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djavadecurity.egd=file:/dev/./urandom","-jar","/app.jar"]