package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject;

public class ChatClient {
	// 필드
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String chatName; // 채팅할 이름 저장
	
	// 메소드 : 서버 연결
	public void connect() throws IOException {
		// 'localhost'에 상대방 IP주면 상대방 것으로 채팅
		socket = new Socket("localhost", 50001);  // => 소켓을 만들며 연결 시도
		dis = new DataInputStream(socket.getInputStream()); // 보조스트림 결합(편리성 향상)
		dos = new DataOutputStream(socket.getOutputStream()); // 보조스트림 결합(편리성 향상)
		System.out.println(" [클라이언트] 서버에 연결됨");
	}
	
	// 메소드 : JSON 받기
	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				while(true) {
					String json = dis.readUTF();
					JSONObject root = new JSONObject(json);
					String clientIp = root.getString("clientIp");
					String chatName = root.getString("chatName");
					String message = root.getString("message");
					System.out.println(" <" + chatName + "@" + clientIp + "> " + message);
				}
			} catch (Exception e) {
				System.out.println(" [클라이언트] 서버 연결 끊김");
				System.exit(0);
			}
		});
		thread.start();
	}
	
	// 메소드 : JSON 보내기
	public void send(String json) throws IOException {
		dos.writeUTF(json);
		dos.flush();
	}
	
	// 메소드 : 서버 연결 종료
	public void unconnect() throws IOException {
		socket.close();
	}
	
	public static void main(String[] arg) {
		try {
			ChatClient chatClient = new ChatClient();
			chatClient.connect();
			
			Scanner scanner = new Scanner(System.in);
			System.out.print(" 대화명 입력 : ");
			chatClient.chatName = scanner.nextLine();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "incoming");
			jsonObject.put("data", chatClient.chatName);
			String json = jsonObject.toString();
			chatClient.send(json);
			
			chatClient.receive();
			
			System.out.println("─────────────────────────────────────────");
			System.out.println(" 보낸 메시지를 입력하고 Enter");
			System.out.println(" 채팅을 종료하려면 'q' 또는 'Q' 입력하고 Enter");
			System.out.println("─────────────────────────────────────────");
			
			// 위에 있는 Scanner로 종료 안 되는 문제 발생하여 새롭게 객체 생성
			Scanner msg = new Scanner(System.in);
			while(true) {
				System.out.print(" ");
				String message = msg.nextLine();
				if (message.toLowerCase().equals("q")) {
					msg.close();
					break;
				} else {
					jsonObject = new JSONObject();
					jsonObject.put("command", "message");
					jsonObject.put("data", message);
					json = jsonObject.toString();
					chatClient.send(json);
					if (message.toLowerCase().equals("q")) {
						msg.close();
					}
				}
			}
			scanner.close();
			chatClient.unconnect();
		} catch (Exception e) {
			System.out.println(" [클라이언트] 서버 연결 안됨");
		}
		System.out.println(" [클라이언트] 종료");
	}
}

