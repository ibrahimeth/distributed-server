<h1>System Programming Homework-II</h1>
<ul>
    <li>
        <p>Using Transport Layer shipping functions with Java programming language
            You are expected to develop a distributed subscription system.</p>
    </li>
    <li>
        <p>This subscription system supports SMTP, HTTP, etc. on the socket. occur within the scope of homework rather than a protocol
        Basic syntax with a primitive protocol called ASUP (Subscription Service Subscription Protocol) It should happen in the order shared below.</p>
    </li>
    <li>
        <p>Load distribution, fault tolerance etc. In the distributed architecture used in requests, each server connects subscribers
        and keeps information about subscribers being online/offline in the system. client from a server
        Subscribe; Must be able to log in to the system from another server.</p>
    </li>
    <li>
        <p>
            After the server list is updated on other servers (the other 2 servers that receive the updated list
            After receiving the "55 TAMM" message from the server), a response must be returned to the client it serves.
        </p>
    </li>
    <li>
        <p>
            Each server accesses its lists during concurrent client access.
            It has to offer thread-safe. (Structures such as lock, synchronized, etc. should be used in critical areas.)
        </p>
    </li>
</ul>
<img src="https://github.com/ibrahimeth/distributed-server/blob/main/assets/sk1.png" alt="picture-first">

<h2>Screen Shots</h2>
<h3>Client</h3>
<img src="https://github.com/ibrahimeth/distributed-server/blob/main/assets/sk3.png" alt="sk" width = "500px">
<h3>Server</h3>
<img src="https://github.com/ibrahimeth/distributed-server/blob/main/assets/sk2.png" alt="sk" width = "500px">
<img src="https://github.com/ibrahimeth/distributed-server/blob/main/assets/sk5.png" alt="sk" width = "500px" >
<img src="https://github.com/ibrahimeth/distributed-server/blob/main/assets/sk4.png" alt="sk"width = "500px" >
