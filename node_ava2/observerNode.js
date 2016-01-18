/** Einlesen der ID des gestarteten Knoten */
var id = process.argv[2];
if(id == undefined){
    console.log("Param 1: ID required");
    return 1;
}

/** Einlesen des Hostfile Pfades */
var hostsFilePath = process.argv[3];
if(id == undefined){
    console.log("Param 2: Path to hosts file");
    return 2;
}

/** Laden der Hostdatei */
var hostslist = require(hostsFilePath)

/**Node Variabeln */
var node;
var neighbours = [];

/** Auslesen der eigenen HostInfos und der Nachbarn */
for(var hostNr in hostslist){
    if(hostslist[hostNr].id == id){
        node = hostslist[hostNr];
    }else{
        neighbours.push(hostslist[hostNr]);
    }
}

/** Lade Clientsocket-packet */
var ioc = require( 'socket.io-client' );
/** Verbinden der Clients */
neighbours.forEach(function (neighbour){
    neighbour.connection = ioc.connect("http://"+neighbour.hostname+ ":" + neighbour.port);
    neighbour.connection.on('responseMoney', function (msg){
        console.log("Node "+neighbour.id+": "+msg.money)
    });
});

/** Konsoleneingabe */
var stdin = process.openStdin();
printCommands();
stdin.addListener("data", function(d) {
    switch (d.toString().trim()){
        case "1":
            initA2();
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "7":
            neighbours.forEach(function (neighbour){
                neighbour.connection.emit('control', {type: "stopPlaying"});
            });
            break;
        case "8":
            neighbours.forEach(function (neighbour){
                neighbour.connection.emit('control', {type: "requestMoney"});
            });
            break;
        default:
            console.log("Unknown command");
    }
    printCommands();
});

function printCommands(){
    console.log("1: Start playing A2");
    console.log("7: Stop playing");
    console.log("8: Request money");
}

function initA2(){
    if(neighbours.length == 8){
        neighbours[0].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'leader', leader: 3, follower: 0}});
        neighbours[1].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'leader', leader: 2, follower: 1}});
        neighbours[2].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'leader', leader: 1, follower: 2}});
        neighbours[3].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'leader', leader: 0, follower: 3}});
        neighbours[4].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'follower', accept: 0}});
        neighbours[5].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'follower', accept: 1}});
        neighbours[6].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'follower', accept: 2}});
        neighbours[7].connection.emit('control', {type: 'init', amountOfHosts: 8, data:{strategy: 'follower', accept: 3}});
    }else if(neighbours.length == 16){
        neighbours[0].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 3, follower: 0}});
        neighbours[1].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 2, follower: 1}});
        neighbours[2].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 1, follower: 2}});
        neighbours[3].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 0, follower: 3}});
        neighbours[4].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 0}});
        neighbours[5].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 1}});
        neighbours[6].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 2}});
        neighbours[7].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 3}});
        neighbours[8].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 3, follower: 0}});
        neighbours[9].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 2, follower: 1}});
        neighbours[10].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 1, follower: 2}});
        neighbours[11].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'leader', leader: 0, follower: 3}});
        neighbours[12].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 0}});
        neighbours[13].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 1}});
        neighbours[14].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 2}});
        neighbours[15].connection.emit('control', {type: 'init', amountOfHosts: 16, data:{strategy: 'follower', accept: 3}});
    }else if(neighbours.length == 24){
        neighbours[0].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 3, follower: 0}});
        neighbours[1].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 2, follower: 1}});
        neighbours[2].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 1, follower: 2}});
        neighbours[3].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 0, follower: 3}});
        neighbours[4].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 0}});
        neighbours[5].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 1}});
        neighbours[6].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 2}});
        neighbours[7].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 3}});
        neighbours[8].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 3, follower: 0}});
        neighbours[9].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 2, follower: 1}});
        neighbours[10].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 1, follower: 2}});
        neighbours[11].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 0, follower: 3}});
        neighbours[12].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 0}});
        neighbours[13].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 1}});
        neighbours[14].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 2}});
        neighbours[15].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 3}});
        neighbours[16].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 3, follower: 0}});
        neighbours[17].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 2, follower: 1}});
        neighbours[18].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 1, follower: 2}});
        neighbours[19].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'leader', leader: 0, follower: 3}});
        neighbours[20].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 0}});
        neighbours[21].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 1}});
        neighbours[22].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 2}});
        neighbours[23].connection.emit('control', {type: 'init', amountOfHosts: 24, data:{strategy: 'follower', accept: 3}});
    }
}

function initA3(){

}