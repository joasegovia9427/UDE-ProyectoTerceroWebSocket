import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

@ServerEndpoint("/webSocketEndPointPartida") ////PARA LA PARTIDA EN SI
public class WebSocketPartida {
//////	Este web socket será  el principal y el único que sera utilizado para jugar.
//////	Este será  singleton, o sea existirá un único websocket para todos los jugadores y partidas que existan 
//////	cuando se inicia el servidor web (que es donde vive este websocket)

	////// SINGLETON
	private static WebSocketPartida webSocketUnicaInstancia; 
	
	////Colección de SESSION, para todo el websocket
	private static Set<Session> webSocketAllSessions = new HashSet<>();
	
	////Colección de {PARTIDA,{SESSION}} para guardar esa asociación, para todo el websocket
	private static Map<String, ArrayList<Session>> webSocketAllPartidaSessions = new HashMap<String, ArrayList<Session>>();
	

	
//	private static HashMap<String, ArrayList<Session>> webSocketAllPartidaSessions = new HashMap<String, ArrayList<Session>>();
//    private static Set<Session> webSocketAllSessions = Collections.synchronizedSet(new HashSet<Session>());
	
    @OnOpen
	public static WebSocketPartida getInstancia(Session in_Session) {  ////antes era public void onOpen(Session session) {
    	msgConsola("******** Entro al @OnOpen getInstancia(Session in_Session) in_Session::" + in_Session.getId() );
    	
    	////agrego la session nueva a una lista generica por las dudas
		webSocketAllSessions.add(in_Session);
		msgConsola("after add webSocketAllSessions.size():"+webSocketAllSessions.size());
		
		////NOTA:: Al hash partidaID,{n sessiones}, lo agrego en el primer on message del cliente
		
		////Singleton::
		if (webSocketUnicaInstancia == null) {
			webSocketUnicaInstancia = new WebSocketPartida();
		}		
		msgConsola("******** Antes del return del Metodo:: getInstancia onOpen con in_Session" );
    	msgConsolaEspacios();
    	
    	msgConsolaMapCompleto();
    	
		return webSocketUnicaInstancia;
		
	}

    
    @OnMessage
    public void onMsg(String in_DatosJSON, Session in_Session) throws JSONException {
    	msgConsola("$$$$$$$$ Entro al @OnMessage onMsg(String in_DatosJSON, Session in_Session)");
    	msgConsola("		DATOS DE ENTRADA:: ");
    	msgConsola("		in_Session.getId()::" + in_Session.getId());
    	msgConsola("		in_DatosJSON::" + in_DatosJSON);
    	
    	String textoRespuestaJson = "";
    	
    	try {
	    	String in_sessionId = in_Session.getId();
	    	
	    	////NOTA: A la lista generica webSocketAllSessions.add(in_Session); la agrego en el getInstancia;
	    	
	    	String in_partidaId = "";
	    	boolean in_isIngresoPorPrimeraVez = false;  ////https://stackoverflow.com/questions/18496372/how-to-parse-json-boolean-value/18496412
	    	
	    	JSONObject jsonDataObject = new JSONObject(in_DatosJSON);
	
	    	jsonDataObject.put("sessionId", in_sessionId);
	    	////Obtengo la in_PartidaId desde el JSON de entrada
	    	////Obtengo si s un ingreso por primera vez.
	    	
	    	in_partidaId = jsonDataObject.getString("partidaId");
	    	msgConsola("in_partidaId::" + in_partidaId);
	    	
//	    	in_isIngresoPorPrimeraVez = (1 == jsonDataObject.getInt("isIngresoPorPrimeraVez"));
//	    	msgConsola("in_isIngresoPorPrimeraVez::" + in_isIngresoPorPrimeraVez);
	    	
	    	in_isIngresoPorPrimeraVez = ((Boolean) jsonDataObject.get("isIngresoPorPrimeraVez")).booleanValue(); //// en el json ingresar algo como "key" : true,
	    	msgConsola("in_isIngresoPorPrimeraVez::" + in_isIngresoPorPrimeraVez);
	
	    	if (!in_partidaId.equals("partida0")) {
				
	    	
		        if (in_isIngresoPorPrimeraVez) {
		        	msgConsola("entro al if (in_isIngresoPorPrimeraVez) {");
		        	////Si es un ingreso por primera vez, me dedico a armar las partida-sessiones
		        	
		        	////Me fijo si ese partida ID esta en el MAP, 
		        	boolean isPartidaEntranteEnMap = false;
		        	isPartidaEntranteEnMap = webSocketAllPartidaSessions.containsKey(in_partidaId);
		        	msgConsola("isPartidaEntranteEnMap::" + isPartidaEntranteEnMap);
		        	
		        	if (!isPartidaEntranteEnMap) { 
		        		////Si NO esta, creo una lista de sessiones, a la cual le agrego esta in_SessionId que entra
		        		ArrayList<Session> sessionesDePartidaNuevaAgregar = new ArrayList<Session>(); ////coleccion de sesiones nueva  a crear con el sessionIdy agregar al map
		        		sessionesDePartidaNuevaAgregar.add(in_Session);
		        		msgConsola("after add sessionesDePartidaNuevaAgregar.size():"+sessionesDePartidaNuevaAgregar.size());
		        		
		        		//// y agrego esta partida
		        		webSocketAllPartidaSessions.put(in_partidaId,sessionesDePartidaNuevaAgregar);
		        		msgConsola("after put Cantidad de elementos del hashmap::"+webSocketAllPartidaSessions.size());
	
						
						////modifico el campo existente, si no funciona, borro el actual y agrego nuevamente jsonDataObject.remove(key);
						//jsonDataObject.put("isIngresoPorPrimeraVez", false);
						//// modifico campo el cual contiene la sessionID, solo para que quede eso... 
		        		//jsonDataObject.put("sessionId", in_sessionId);
		        		
						
//						for (int c = 0; c <= 10; c++) {
//			        		jsonDataObject.put("contador", c);
//			        		
							////retorno mi JSON a mi in_Session
							//textoRespuestaJson = jsonDataObject.toString();
							//msgConsola("textoRespuestaJson::"+textoRespuestaJson);
							///si entre por primera vez a mi mismo me mando mis datos para confirmar y para que guarde mi sessID
							//in_Session.getBasicRemote().sendText(textoRespuestaJson);
//			                Thread.sleep(100);
//			            }
						
					} else {
		    			////Si esta, entonces busco esa partida por el id y obtengo su lista de sessiones
						ArrayList<Session> sessionesDePartidaActualObtenida = new ArrayList<Session>(); ////coleccion de sesiones de la partida en la que busque
						sessionesDePartidaActualObtenida = (ArrayList<Session>) webSocketAllPartidaSessions.get(in_partidaId);
						msgConsola("sessionesDePartidaActualObtenida.size():"+sessionesDePartidaActualObtenida.size());
		        		
						
						boolean isSessionEntranteEnLista = false;
						isSessionEntranteEnLista = sessionesDePartidaActualObtenida.contains(in_Session);
			        	
						if (isSessionEntranteEnLista) {
							////Si mi in_SessionId esta en la lista, no hago nada (imprimo en consola que esta session ya esta ingresada, por lo que hay un error, ya que nunca deberia entrar aqui..
							msgConsola("esta session ya esta ingresada, por lo que hay un error, ya que nunca deberia entrar aqui.. in_sessionId::"+in_sessionId);
						} else {
				        	////Sino, o sea, mi in_SessionId NO esta en la lista, la agrego a esa lista de sessiones de la partida.
							sessionesDePartidaActualObtenida.add(in_Session);
							msgConsola("after add sessionesDePartidaActualObtenida.size():"+sessionesDePartidaActualObtenida.size());
			        		
							
							////Y luego sustituyo con la lista actual
							webSocketAllPartidaSessions.replace(in_partidaId, sessionesDePartidaActualObtenida);
							
							////retorno en un tercer, que quedo ingresada en el arbol y que ya no vuelva a mandar ese dato de primera vez en el json
							
							////modifico el campo existente, si no funciona, borro el actual y agrego nuevamente jsonDataObject.remove(key);
							//jsonDataObject.put("isIngresoPorPrimeraVez", false);
							
							//// y un cuarto campo el cual contiene la sessionID, solo para que quede eso... 
							//jsonDataObject.put("sessionId", in_sessionId);
							
							
//							for (int c = 0; c <= 10; c++) {
//				        		jsonDataObject.put("contador", c);
//				        		
								////retorno mi JSON a mi in_Session
								//textoRespuestaJson = jsonDataObject.toString();
								//msgConsola("textoRespuestaJson::"+textoRespuestaJson);
	
								//in_Session.getBasicRemote().sendText(textoRespuestaJson);
								
//				                Thread.sleep(100);
//				            }
							
						}
					}
		        	
				} else {
			    	////Si no es un ingreso por primera vez, continuo para mandar datos al resto correspondientes
			    	//// si no es primer vez, significa que ésta in_SessionId esta en la lista interna de una partida
					///busco la partida en el map con el in_partidaId
					////obtengo la lista de sessiones internas
					ArrayList<Session> sessionesDePartidaActualObtenida = new ArrayList<Session>(); ////coleccion de sesiones de la partida en la que busque
					sessionesDePartidaActualObtenida = (ArrayList<Session>) webSocketAllPartidaSessions.get(in_partidaId);
					
			    	////y para cada session DISTINTA a la mia, le mando mi json de datos, referentes a como armar mi partida y mis objetos del otro lado (mi enemigo puede armar todo)
		    	    msgConsola("Aca es si repitio");
		    	    
					for (Session sessionActual : sessionesDePartidaActualObtenida) { ////recorro todas las sesiones de esa partida
		    	    	if ( !sessionActual.getId().equals(in_sessionId) ) { ////si la sesion en la que me paro NO es igual a la que entra, le mando mis datos
		    	    		msgConsola("enviar a sessionActual::"+sessionActual.getId());
						
							jsonDataObject.put("sessionId", in_sessionId);// sessionActual.getId()); 
		    	    		
//		    	    		for (int c = 0; c <= 10; c++) {
//		    	    			
//				        		jsonDataObject.put("contador", c);
//				        		
								////retorno mi JSON a mi in_Session
								textoRespuestaJson = jsonDataObject.toString();
								msgConsola("textoRespuestaJson::"+textoRespuestaJson);
	
								sessionActual.getBasicRemote().sendText(textoRespuestaJson);
								
//				                Thread.sleep(100);
//				            }
		    	    		
		    	    		
						}
		    	    }
			    	
				}
			}
	
	        Thread.sleep(100);
	        
        
		} catch (Exception e) {
			 msgConsola("catch (Exception e)::"+ e.getMessage());
		}

        msgConsola("$$$$$$$$ Salio del @OnMessage void onMsg(String in_DatosJSON, Session in_Session)");
        msgConsolaEspacios();
        
        
        msgConsolaMapCompleto();
    }
    
   
    @OnClose
    public void onClose(Session in_Session) {
    	msgConsola("======== Entro al @OnClose (Session in_Session) in_Session::"+ in_Session.getId());
    	
    	////remuevo la session de la lista generica
    	webSocketAllSessions.remove(in_Session);
    	msgConsola("after delete webSocketAllSessions.size():"+webSocketAllSessions.size());
		
    	ArrayList<String> listaDePartidasABorrar = new ArrayList<>(); 
    	
    	
    	boolean isElimine = false;
    	//String idPartidaActualRecorrida = " ";
    	ArrayList<Session> sessionesDePartidaActualRecorrida = new ArrayList<>(); ////coleccion de sesiones de la partida en la que estoy parado
    	msgConsola("Cantidad de elementos del hashmap antes::"+webSocketAllPartidaSessions.size());
    	for (Map.Entry<String, ArrayList<Session>> entry : webSocketAllPartidaSessions.entrySet()) {
    	    //idPartidaActualRecorrida = entry.getKey();
    	    sessionesDePartidaActualRecorrida = (ArrayList<Session>) entry.getValue();
    	    msgConsola("Cantidad de elementos de la subcoleccion antes::"+sessionesDePartidaActualRecorrida.size());
    	    
    	    ArrayList<Session> listaDeSessionesABorrar = new ArrayList<>(); 
    	    isElimine = false;
    	    for (Session sessionActual : sessionesDePartidaActualRecorrida) { ////recorro todas las sesiones de esa partida
    	    	if (sessionActual.getId().equals(in_Session.getId())) { ////si la sesion en la que me paro es igual a la que entra la elimino
    	    		listaDeSessionesABorrar.add(in_Session);
    	    		
    	    	}
    	    }
    	    
    	    for (Session sessionActual : listaDeSessionesABorrar) {
    	    	sessionesDePartidaActualRecorrida.remove(sessionActual);
    	    	isElimine = true;
    	    }
    		
    		msgConsola("Cantidad de elementos de la subcoleccion luego de borrar::"+sessionesDePartidaActualRecorrida.size());
		
//    	    if (isElimine) { ////si elimine una session, le aviso a los demas que esa persona se fue.
//        	    for (Session sessionActual : sessionesDePartidaActualRecorrida) {
//        	    	try {
//        	    		JSONObject jsonDataOut = new JSONObject();
//        	    		jsonDataOut.put("fromOnClose::in_Session.getId()=closeSession", in_Session.getId() );
//        	    		String textoRespuestaJson = jsonDataOut.toString();
//					   	sessionActual.getBasicRemote().sendText(textoRespuestaJson); 
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//        	    }
//			}
    	    msgConsola("Cantidad de elementos de la subcoleccion luego::"+sessionesDePartidaActualRecorrida.size());
    	    if (sessionesDePartidaActualRecorrida.size()==0) { ////si luego de borrar la lista de sessions quedo sin sesiones, o sea sin jugadores, elimino la partida
    	    	listaDePartidasABorrar.add(entry.getKey());
    	    }
    	}
    	
    	
	    for (String idPartidaABorrar : listaDePartidasABorrar) { ////recorro todas las sesiones de esa partida
	    	webSocketAllPartidaSessions.remove(idPartidaABorrar);
	    }
    	
    	
    	msgConsola("Cantidad de elementos del hashmap despues::"+webSocketAllPartidaSessions.size());
	    
    	
    	////recorro todas las partidas, entro en cada subcoleccion y si esta esa session la elimino...
    	///luego de borrar si la subcoleccion quedo en 0, remuevo la partida del hash...    	
    	
    	
    	////cuando un jugador de la subcoleccion cierra, a las otras avisarles desde aca con
    	///s.getBasicRemote().sendText("usuario x cerro");
    	msgConsola("======== Salio del Metodo::public void onClose(Session in_Session)");
    	msgConsolaEspacios();
    	
    	
    	msgConsolaMapCompleto();
    }
    
    @OnError
    public void onError(Throwable t) {
    	msgConsola("######## Entro al Metodo:: public void onError(Throwable t)");
    	msgConsola("onError Throwable t::" + t.getMessage());
    	msgConsola("######### Salio del Metodo:: public void onError(Throwable t)");
    	msgConsolaEspacios();
    }
    
    
    ////imprimir el estado del map con sus sessiones
    public static void msgConsolaMapCompleto() {
    	msgConsolaEspacios();
    	msgConsola("(((((((((((( Entro al msgConsolaMapCompleto");
    	String idPartidaActualRecorrida;
    	
    	ArrayList<Session> sessionesDePartidaActualRecorrida = new ArrayList<>(); ////coleccion de sesiones de la partida en la que estoy parado
    	msgConsola("Cantidad de elementos del hashmap::"+webSocketAllPartidaSessions.size());
    	for (Map.Entry<String, ArrayList<Session>> entry : webSocketAllPartidaSessions.entrySet()) {
    		msgConsola("__________________________________________________________");
			
    		idPartidaActualRecorrida = entry.getKey();
    		msgConsola(".	idPartidaActualRecorrida::"+idPartidaActualRecorrida);
			
    		sessionesDePartidaActualRecorrida = (ArrayList<Session>) entry.getValue();
    	    msgConsola(".			Cantidad de elementos de la subcoleccion::"+sessionesDePartidaActualRecorrida.size());
    	    
    	   
    	    for (Session sessionActual : sessionesDePartidaActualRecorrida) { ////recorro todas las sesiones de esa partida
    	    	msgConsola(".				sessionActual::"+sessionActual.getId());
    	    }
    	}
    	msgConsola("Cantidad de elementos del hashmap::"+webSocketAllPartidaSessions.size());
	    
    	msgConsola(")))))))))))) Salio del msgConsolaMapCompleto ");
    	msgConsolaEspacios();
    }
    
    ////Pasar estos 3 a un utilitarios luego en el proyecto general
    public static void msgConsola(String in_msgToConsole) {
    	//printCompleteDateTime();
//        System.out.println(in_msgToConsole.trim());
    }

    public static void msgConsolaEspacios() {
//        System.out.println(" ");
//        System.out.println(" ");
//        System.out.println(" ");
    }
    
	public static void printCompleteDateTime() {
		System.out.print(getCompleteDateTime()+"=>");
//		System.out.print(new Date().toString()+"==>");
	}
		
	public static String getCompleteDateTime() {
		String YMDHMS = "";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		YMDHMS = (String) (dtf.format(now));
		
		return YMDHMS;
	}
}