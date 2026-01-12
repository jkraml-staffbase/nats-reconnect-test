## Grace Period Behavior
- after lame duck mode starts, it stops accepting new connections
- it wait the grace period and starts closing client connections
- after the last client connection is closed, the server shuts down, even if the lame duck duration is not over yet


## Grace Period vs. Clients Connected

- if no clients are connected, the server shuts down immediately when lame duck mode starts
- if clients are connected when starting lame duck mode, but they disconnect, the server still waits for the grace period before shutting down
- i.e. the grace period only "exists" when clients were there. Probably makes sense so that they can react to it


## Lame Duck Mode TCP Behavior

- when in lame duck mode, the server does not accept new connections on the TCP level (as opposed to declining the connection requests on the application layer). To the client, it looks like it's not there at all.


## Reconnects: Plain NATS vs JetStream

...
