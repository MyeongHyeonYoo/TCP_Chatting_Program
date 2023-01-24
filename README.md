# **Project - TCP Chatting Program**  🗯️💭🗨️

 ## 💡 `Java` PROJECT <img src="https://img.shields.io/badge/Java-17-purple">


---

### 🧾 프로젝트 소개 

---

>**ECLIPSE 환경 구축**

> 1. ECLIPSE 설치
> 2. JDK 설치
> 3. JSON 라이브러리 설치(Build Path)<br>
https://repo1.maven.org/maven2/org/json/json/20220924/ (MAVEN REPOSITORY) <br>
https://github.com/stleary/JSON-java (Git Hub)

---


#### ◾ Chatting Server 구축 완료

- 서버 시작<br>
<img src="img/server_start.jpg" width="500" height="300">

- 서버 1명 입장<br>
<img src="img/server_chatter_add_1.jpg" width="500" height="300"><br>
(0명 입장은 클라이언트 쪽에서 종료)

##### 종료 확인 ('q' | 'Q')
<img src="img/exit_lower_q.jpg" width="400" height="200">
<img src="img/exit_upper_q.jpg" width="400" height="200">

---

#### ◾ Client Server 구축 완료

- 클라이언트 시작<br>
<img src="img/client_start.jpg" width="500" height="300">

- 클라이언트 입력(입장)<br>
<img src="img/client_input.jpg" width="500" height="300">

---

##### 종료 확인 ('q' | 'Q')
<img src="img/exit_client_lower_q.jpg" width="400" height="200">
<img src="img/exit_client_upper_q.jpg" width="400" height="200">

---

#### ◾ Catting
<img src="img/client2_enter.jpg" width="500" height="300"><br><br>
서버 상태<br>
<img src="img/server_status.jpg" width="500" height="300"><br><br>
<img src="img/winter_chatting.jpg" width="500" height="300"><br>
<img src="img/summer_chatting.jpg" width="500" height="300"><br><br>
클라이언트 종료(나가기)<br>
<img src="img/client_exit.jpg" width="700" height="400">


```
rest time :D

종료 확인 ('q' | 'Q')
toLowerCase()와 equals() 메소드를 활용하여 구현

┌──────────────────────────────────────────────┐
│ 클라이언트 버그 존재                          │
│ 채팅 전 바로 q 또는 Q누르면 서버가 종료되지만, │ 
│ 채팅 후 q 또는 Q누르면 종료 안 되는 현상 존재  │
└──────────────────────────────────────────────┘
```


