package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

public class ChatServer {
	// 필드
	ServerSocket serverSocket;
	ExecutorService threadPool = Executors.newFixedThreadPool(100);
	// Hashtable을 쓸 수 있지만 여기서는 HashMap을 동기화 시켜 사용
	// Hashtable은 공동 객체에 동기화 하여 사용하기 때문에 멀티 스레드에서 사용하기 최적화
	Map<String, SocketClient> chatRoom =
			Collections.synchronizedMap(new HashMap<>());
	// Map<String, SocketClient> chatRoom = new Hashtable<>(); 
	
	// 메소드 : 서버 시작
	public void start() throws IOException {
		serverSocket = new ServerSocket(50001);
		System.out.println(" [서버] 시작 됨");
		
		// cf) 익명 객체 직접 만들기
		/*
		 * Thread thread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { try { Socket socket = serverSocket.accept();
		 * SocketClient sc = new SocketClient(this, socket); // this => Runnable } catch
		 * (Exception e) {} } });
		 */
				
		
		Thread thread = new Thread(() -> {
			try {
				while(true) {
					Socket socket = serverSocket.accept();
					SocketClient sc = new SocketClient(this, socket); // this => ChatServer 
					//SocketClient sc = new SocketClient(ChatServer.this, socket); // 직접 명시해서 this 쓸 수 있다.
				}
			} catch (Exception e) {}
		}); 
		thread.start();
	}
	
	// 메소드 : 클라이언트 연결 시 SocketClient 생성 및 추가
	// 채팅자 추가
	public void addSocketClient(SocketClient socketClient) {
		String key = socketClient.chatName + "@" + socketClient.clientIp;
		chatRoom.put(key, socketClient);
		System.out.println(" 입장 : " + key);
		System.out.println(" 현재 채팅자 수 : " + chatRoom.size() + "\n");
	}
	
	// 메소드 : 클라이언트 연결 종료 시 SocketClient 제거
	// 채팅자 퇴장
	public void removeSocketClient(SocketClient socketClient) {
		String key = socketClient.chatName + "@" + socketClient.clientIp;
		chatRoom.remove(key);
		System.out.println(" 퇴장 : " + key);
		System.out.println(" 현재 채팅자 수 : " + chatRoom.size() + "\n");
	}
	
	// 메소드 : 모든 클라이언트에게 메시지 보냄
	// 발신자
	// 메시지 보낸 사람(발신자)에게는 보내지 않음.
	public void sendToAll(SocketClient sender, String message) {
		JSONObject root = new JSONObject();
		// 대소문자 구분 필요.
		// 똑같은 이름으로 받아오기 위해
		root.put("clientIp", sender.clientIp);
		root.put("chatName", sender.chatName);
		root.put("message", message);
		String json = root.toString();
		
		Collection<SocketClient> socketClients = chatRoom.values();
		for (SocketClient sc : socketClients) { // 외부 반복자
			if (sc == sender) continue; // 객체 동등하면 보내지 말고 continue
			sc.send(json);
		}
	}
	
	// 메소드 : 서버 종료
	public void stop() {
		/*try {
			serverSocket.close();
			threadPool.shutdownNow();
			Collection<SocketClient> socketClients = chatRoom.values();
			// 외부 반복자(for) 사용
			for(SocketClient sc : socketClients) {
				sc.close();
			}
		} catch (Exception e) {} */
		
		try {
			serverSocket.close();
			threadPool.shutdownNow();
			// 내부 반복자(stream) 사용
			chatRoom.values().stream().forEach(sc -> sc.close());
			System.out.println(" [서버] 종료 ");
		} catch (Exception e) {}
	}
	
	// 메소드 : 메인
	public static void main(String[] args) {
		try {
			ChatServer chatServer = new ChatServer();
			chatServer.start();
			
			System.out.println("─────────────────────────────────────────────────────");
			System.out.println(" 서버를 종료하려면 'q'또는 'Q'를 입력하고 Enter키를 입력하세요.");
			System.out.println("─────────────────────────────────────────────────────");
			
			Scanner scanner = new Scanner(System.in);
			while(true) {
				System.out.print(" ");
				String key = scanner.nextLine();
				if (key.toLowerCase().equals("q")) break;
			}
			scanner.close();
			chatServer.stop(); // TCP 서버 종료
		} catch (Exception e) {
			System.out.println(" [서버] " + e.getMessage());
		}
	}

}
