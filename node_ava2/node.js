/** Einlesen der ID des gestarteten Knoten */
var id = process.argv[2];
if(id == undefined){
    console.log("Param 1: ID required");
    return 1;
}

/** Laden der Hostdatei */
var hostslist = require('./hostlist_a2_24.json');

/**Node Variabeln */
var node;
var neighbours = [];
var amountOfHosts = 0;

/**Game Variabeln */
var money = 0;
var nodeMode;
var amountOfPlayersToContact = 3;
var continuePlaying = false;

/** Auslesen der eigenen HostInfos und der Nachbarn */
for(var hostNr in hostslist){
    if(hostslist[hostNr].id == id){
        node = hostslist[hostNr];
    }else{
        neighbours.push(hostslist[hostNr]);
    }
}

/** Starte Server */
var io = require('socket.io')(node.port);
console.log("Started server:"+node.hostname+ ":" + node.port);
io.on('connection', function (socket) {
    /** Kontrollnachrichten abfangen */
    socket.on('control', function (msg) {
        switch (msg.type){
            case "init":
                amountOfHosts = msg.amountOfHosts;
                money = 0;
                continuePlaying = true;
                nodeMode = msg.data;
                break;
            case "startPlaying":
                startPlaying();
                break;
            case "stopPlaying":
                continuePlaying = false;
                break;
            case "requestMoney":
                socket.emit('responseMoney', {money: money});
                break;
            default:
                console.log("Unknown control type: "+msg.type);
        }
    });
    /** Spielnachrichten abfangen */
    socket.on('game', function (msg){
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
                        break;
                    case 'dynamic':
                        break;
                    default :
                        console.log('Unknown strategy: ' + gameMode);
                }
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
        switch (msg.type){
            case 'accepted':
                money += msg.data.leader;
                break;
            default:
                console.log('Unknown game type: '+msg.type);
        }
    });
});

/** Starte das Spiel mit einer definierten Anzahl an Clients */
function startPlaying(){
    if(continuePlaying){
        for(var i = 0; i < amountOfPlayersToContact; i++){
            setTimeout(function(){
                var rndIndex = Math.floor(Math.random()*amountOfHosts);
                neighbours[rndIndex].connection.emit('game', {type: 'request', data: {leader: nodeMode.leader, follower: nodeMode.follower}});
            }, i*100);
        }
    }
}

