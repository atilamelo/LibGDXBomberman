Para rodar o jogo no modo multiplayer, é necessário rodar primeiramente o DebugServerLauncher. 

Ele abrirá o servidor Kryonet, mas caso tenha algum problema para rodar devido a porta ocupada, é possível
mudá-la na pasta networking em core, no arquivo Network nos parâmetros UDP port e TCP port. 

Após abrir o servidor, rode o DesktopLauncher que abrirá uma instância cliente do servidor.

Este jogo considera um ambiente localhost, portanto é o endereço usado por padrão no Network.

A versão de Java testada para rodar o servidor foi o Java 11.0.1. 
Gradle 6.9.1, mas qualquer versão compatível com Java 11 deve funcionar.
