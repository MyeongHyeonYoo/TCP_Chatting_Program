package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONObject;

public class SocketClient {
	// 필드
	// 최소한 default 접근 제한을 위해 private 사용 금지
	// default 접근 제한자 : 같은 패키지 내 접근 가능
	ChatServer chatServer;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String clientIp;
	String chatName;
	
	// 생성자
	public SocketClient(ChatServer chatServer, Socket socket) {
		try {
		this.chatServer = chatServer;
		this.socket = socket;
		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());
		// InetSocketAddress -> Inet : Internet
		// => 인터넷 소캣의 주소 정보 - 인터넷에서 사용하는 소캣
		InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
		this.clientIp = isa.getHostName();
		// API Document 확인
		// this.clientIp = isa.getHostString(); // -> getHostStirng()으로 써도 된다.
		receive();
		} catch (Exception e) {}
	}

	// 클라이언트가 보낸 데이터(메시지) 받기
	// this => SocketClient
	private void receive() {
		chatServer.threadPool.execute(() -> {
			try {
				while(true) {
					// {"command" : "xxxx", ...}
					// {"command" : "incoming", "data": "chatName"}
					// {"command" : "message", "data": "xxxx(메시지 내용)"}
					String receiveJson = dis.readUTF();
					
					JSONObject jsonObject = new JSONObject(receiveJson);
					String command = jsonObject.getString("command");
					
					switch (command) {
						case "incoming": 
							this.chatName = jsonObject.getString("data");
							chatServer.sendToAll(this, " 들어왔습니다.");
							chatServer.addSocketClient(this);
							break;
						case "message":
							String message = jsonObject.getString("data");
							chatServer.sendToAll(this, message);
							// sendToAll method의 this 타입 확인 -> SocketClient 타입
							break;
					}
				}
			} catch (IOException e) {
				chatServer.sendToAll(this, "나갔습니다.");
				chatServer.removeSocketClient(this);
			}
		});
		
	}
	
	
	// 클라이언트에게 데이터(메시지) 보내기
	public void send(String json) {
		try {
			dos.writeUTF(json);
			dos.flush();
			// 재사용 계속 해야되기 때문에 flush 까지 (close 해주면 안 된다.)
		} catch (Exception e) {}
		
	}

	// 더 이상 통신하지 않을 때 닫기
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {}
	}
	
	
}
