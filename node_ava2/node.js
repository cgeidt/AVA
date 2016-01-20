/** Einlesen der ID des gestarteten Knoten */
var id = process.argv[2];
if(id == undefined){
    console.log("Param 1: ID required");
    return 1;
}

/** Laden der Hostdatei */
var hostslist = require('./hostlist.json');

/**Node Variabeln */
var node;
var neighbours = [];

var sendCounter = 0;
var receiveCounter = 0;

/**Game Variabeln */
var money = 0;
var nodeMode;
var continuePlaying = false;
var moneyLimit = null;
var halt = false;

/** Auslesen der eigenen HostInfos und der Nachbarn */
for(var hostNr in hostslist){
    if(hostslist[hostNr].id == id){
        node = hostslist[hostNr];
    }else{
        neighbours.push(hostslist[hostNr]);
    }
}

var amountOfPlayers = neighbours.length;

/** Starte Server */
var io = require('socket.io')(node.port);
console.log("Started server:"+node.hostname+ ":" + node.port);
io.on('connection', function (socket) {
    /** Kontrollnachrichten abfangen */
    socket.on('control', function (msg) {
        switch (msg.type){
            /** Initialisiere Knoten im entsprechenden Spielmodi */
            case "init":
                reset();
                amountOfPlayers = msg.amountOfPlayers;
                if(msg.moneyLimit != undefined){
                    moneyLimit = msg.moneyLimit;
                }
                nodeMode = msg.data;
                console.log(nodeMode);
                break;
            /** Setze Spieldaten zurück */
            case "reset":
                reset();
                break;
            /** Beginne zu spielen */
            case "startPlaying":
                startPlaying();
                break;
            /** Höre auf zu spielen */
            case "stopPlaying":
                continuePlaying = false;
                break;
            /** Liefere Geld zurück */
            case "requestMoney":
                socket.emit('responseMoney', {money: money});
                break;
            /** Teile Sende- und Empfangszahlen */
            case "doubleCountCheck":
                socket.emit('doubleCountResponse', {transferredMessages: receiveCounter+sendCounter});
                break;
            default:
                console.log("Unknown control type: "+msg.type);
        }
    });
    /** Spielnachrichten abfangen */
    socket.on('game', function (msg){
        receiveCounter++;
        switch (msg.type){
            case 'request':
                    switch (nodeMode.strategy) {
                        case 'follower':
                            if (nodeMode.accept <= msg.data.follower){
                                money += msg.data.follower;
                                socket.emit('game', {type: 'accepted', data: msg.data});
                            }
                            break;
                        case 'leader':
                            startPlaying();
                            break;
                        case 'both':
                            /** Angehalten aufgrund von Vermögensgrenze? */
                            if(halt){
                                socket.emit('game', {type: 'halt'});
                            }else{
                                if (nodeMode.accept <= msg.data.follower){
                                    money += msg.data.follower;
                                    if(moneyLimit != null && money >= moneyLimit){
                                        halt = true;
                                        continuePlaying = false;
                                        socket.emit('game', {type: 'halt'});
                                        //console.log("Send HALT: "+money);
                                    }else{
                                        socket.emit('game', {type: 'accepted', data: msg.data});
                                    }
                                }
                                startPlaying();
                            }
                            break;
                        default :
                            console.log('Unknown strategy: ' + gameMode);
                    }
                break;
            /** Empfange HALT-Nachricht */
            case 'halt':
                halt = true;
                continuePlaying = false;
                //console.log("Received: HALT");
                break;
            default:
                console.log('Unknown game type: '+msg.type);
        }
    });
});

/** Lade Clientsocket-packet */
var ioc = require( 'socket.io-client' );

/** Verbinden der Clients */
neighbours.forEach(function (neighbour){
    neighbour.connection = ioc.connect("http://"+neighbour.hostname+ ":" + neighbour.port);
    neighbour.connection.on('game', function (msg){
        receiveCounter++;
        switch (msg.type){
            case 'accepted':
                if(!halt){
                    money += msg.data.leader;
                    /** Vermögensgrenze erreicht? Wenn ja sende HALT */
                    if(moneyLimit != null && money >= moneyLimit){
                        halt = true;
                        continuePlaying = false;
                        neighbour.connection.emit('game', {type: 'halt'});
                        //console.log("Send HALT: "+money);
                    }
                }
                break;
            /** Empfange HALT-Nachricht */
            case 'halt':
                halt = true;
                continuePlaying = false;
                break;
            default:
                console.log('Unknown game type: '+msg.type);
        }
    });
});

/** Starte das Spiel mit einer definierten Anzahl an Clients */
function startPlaying(){
    for(var i = 0; i < Math.ceil(amountOfPlayers/2); i++){
        setTimeout(function(){
            /** Spiel angehalten? */
            if(continuePlaying) {
                sendCounter++;
                var rndIndex = Math.floor(Math.random()*(amountOfPlayers-1));
                neighbours[rndIndex].connection.emit('game', {type: 'request', data: {leader: nodeMode.leader, follower: nodeMode.follower}});
            }
        }, i*(200-(i*2)));
    }
}

/** Setze Spielparamter zurück */
function reset(){
    money = 0;
    sendCounter = 0;
    receiveCounter = 0;
    halt = false;
    continuePlaying = true;
    moneyLimit = null;
}