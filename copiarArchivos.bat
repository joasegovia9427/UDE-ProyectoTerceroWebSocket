@echo off

::ORIGENES 
::En la siguiente variable ORIGEN_CLASES, se ingresa el path del origen de las CLASS, esta debe terminar la contrabarra
SET ORIGEN_CLASES="C:\GitHub\Proyectos\Proyecto\PruebaConceptoCentripio\work"
::En la siguiente variable ORIGEN_JS, se ingresa el path del origen de los JS para copiar, esta debe terminar sin la contrabarra
SET ORIGEN_JS="C:\GitHub\Proyectos\Proyecto\PruebaConceptoCentripio\WEB-INF"

::DESTINOS ______________________________________________________
::En la siguietne variable DESTINO_TOMCAT_CLASS, se ingresa el destino del Tomcat
SET DESTINO_TOMCAT_CLASS="C:\Program Files\Apache Software Foundation\Tomcat 9.0_Tomcat9.0.13\webapps\prueba\WEB-INF\classes"
::En la siguietne variable DESTINO_TOMCAT_JS, se ingresa el destino del Tomcat
SET DESTINO_TOMCAT_JS="C:\Program Files\Apache Software Foundation\Tomcat 9.0_Tomcat9.0.13\webapps\prueba"

SET DESTINO_FRONT_CLASS="C:\Users\joase\Documents\GitHub\UDEProyectoTercero\FrontEnd\JuegoSaviorsOfTheSomme\src\WebSocket"


echo **********Iniciando copia de archivos**********

cd %ORIGEN_CLASES%
xcopy *.class %DESTINO_TOMCAT_CLASS% /a /h /y

xcopy WebSocketPartida.class %DESTINO_FRONT_CLASS% /a /h /y
xcopy WebSocketPartida.java %DESTINO_FRONT_CLASS% /a /h /y


cd %ORIGEN_JS%
xcopy *.js %DESTINO_TOMCAT_JS% /a /h /y
xcopy *.html %DESTINO_TOMCAT_JS% /a /h /y
xcopy *.css %DESTINO_TOMCAT_JS% /a /h /y


echo **********Fin de copia**********