# Definição do protocolo

Esta aplicação utiliza dois protocolos de comunicação personalizados, um para mensagens internas (assim threads se comunicam entre si) e outro para comunicação com outros sistemas.

## Protocolo de comunicação interna

Para a comunicação interna, a aplicação utiliza números inteiros como identificadores de mensagens.

São eles:

- 0: Mensagem para `Terminar o processo do programa`
- 1: Mensagem para ``
- 2: Mensagem para ``

## Protocolo de comunicação externa

Para a comunicação externa, a aplicação utiliza símbolos como identificadores de mensagens.

São eles:

- ♥ Mensagem de HEARTBEAT  <!-- Alt + 3    -->
- ¶ Mensagem de TALK       <!-- Alt + 0182 -->
- ├ Mensagem de FILE       <!-- Alt + 195  -->
- ─ Mensagem de CHUNK      <!-- Alt + 196  -->
- ┤ Mensagem de END        <!-- Alt + 180  -->
- ­­= Mensagem de ACK        <!-- Equal      -->
- ¬ Mensagem de NACK       <!-- Alt + 170  -->
