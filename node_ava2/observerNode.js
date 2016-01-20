/** Einlesen der ID des gestarteten Knoten */
var id = process.argv[2];
if (id == undefined) {
    console.log("Param 1: ID required");
    return 1;
}


/** Laden der Hostdatei */
var hostslist = require('./hostlist.json')

/**Node Variabeln */
var node;
var neighbours = [];

var nodeWithMostMoney;
var answerCounter = 1;
var doubleCheckNodeResponseCounter = 1;
var doubleCheckCounter = 0;
var doubleCheckCounterOld =0;

/** Auslesen der eigenen HostInfos und der Nachbarn */
for (var hostNr in hostslist) {
    if (hostslist[hostNr].id == id) {
        node = hostslist[hostNr];
    } else {
        neighbours.push(hostslist[hostNr]);
    }
}

/** Lade Clientsocket-packet */
var ioc = require('socket.io-client');

/** Verbinden der Clients */
neighbours.forEach(function (neighbour) {
    neighbour.connection = ioc.connect("http://" + neighbour.hostname + ":" + neighbour.port);
    neighbour.connection.on('responseMoney', function (msg) {
        if (nodeWithMostMoney == null || nodeWithMostMoney.money < msg.money) {
            nodeWithMostMoney = {id: neighbour.id, money: msg.money, strategy: neighbour.strategy};
        }
        console.log("Node " + neighbour.id + " " + neighbour.strategy + ": " + msg.money);
        if (++answerCounter == neighbours.length) {
            console.log("Most successful: Node " + nodeWithMostMoney.id + " " + nodeWithMostMoney.strategy + ": " + nodeWithMostMoney.money);
            printCommands();
        }
    });
    neighbour.connection.on('doubleCountResponse', function (msg) {
        doubleCheckCounter += msg.transferredMessages;
        doubleCheckNodeResponseCounter++;
        console.log(doubleCheckCounterOld);
        console.log(doubleCheckCounter);
        if (doubleCheckNodeResponseCounter == neighbours.length) {
            if(doubleCheckCounterOld == doubleCheckCounter){
                requestMoney();
            }else{
                doubleCheckCounterOld = doubleCheckCounter;
                setTimeout(startDoubleCountCheck, 1000);
            }
        }
    });
});
/** Konsoleneingabe */
var stdin = process.openStdin();
printCommands();
stdin.addListener("data", function (d) {
    switch (d.toString().trim()) {
        case "1":
            initA2(8);
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "2":
            initA2(16);
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "3":
            initA2(24);
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "4":
            initA3(16);
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "5":
            initA3(32);
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "6":
            initA3(48);
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "7":
            initA4();
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            break;
        case "8":
            initA5();
            neighbours[0].connection.emit('control', {type: "startPlaying"});
            doubleCheckCounter = 0;
            doubleCheckCounterOld = 0;
            doubleCheckNodeResponseCounter = 1;
            setTimeout(startDoubleCountCheck, 1000);
            break;
        case "9":
            neighbours.forEach(function (neighbour) {
                neighbour.connection.emit('control', {type: "stopPlaying"});
            });
            break;
        case "10":
            requestMoney();
            break;
        default:
            console.log("Unknown command");
    }
    printCommands();
});

/** Ausgeben der Befehlliste */
function printCommands() {
    console.log("1: Start playing A2 (8)");
    console.log("2: Start playing A2 (16)");
    console.log("3: Start playing A2 (24)");
    console.log("4: Start playing A3 (16)");
    console.log("5: Start playing A3 (32)");
    console.log("6: Start playing A3 (48)");
    console.log("7: Start playing A4");
    console.log("8: Start playing A5");
    console.log("9: Stop playing");
    console.log("10: Request money");
}

/** Initialisiere Knoten für A2 */
function initA2(amountOfPlayers) {
    resetNodes();
    var leader = 3;
    var acceptLimit = 3;
    var hostIndex = 0;

    var leaderStrategies = ["(L-A)", "(L-B)", "(L-C)", "(L-D)"];
    var followerStrategies = ["(F-A)", "(F-B)", "(F-C)", "(F-D)"];
    while (hostIndex < amountOfPlayers - 1) {
        for (var follower = 0; follower <= 3; follower++) {
            neighbours[hostIndex].strategy = leaderStrategies[follower];
            neighbours[hostIndex++].connection.emit('control', {
                type: 'init',
                amountOfPlayers: amountOfPlayers,
                data: {strategy: 'leader', leader: leader - follower, follower: follower}
            });
        }
        for (var accept = 0; accept <= acceptLimit; accept++) {
            neighbours[hostIndex].strategy = followerStrategies[accept];
            neighbours[hostIndex++].connection.emit('control', {
                type: 'init',
                amountOfPlayers: amountOfPlayers,
                data: {strategy: 'follower', accept: accept}
            });
        }
    }
}

/** Initialisiere Knoten für A3 */
function initA3(amountOfPlayers) {
    resetNodes();
    var leader = 3;
    var acceptLimit = 3;
    var hostIndex = 0;

    var leaderStrategies = ["(L-A)", "(L-B)", "(L-C)", "(L-D)"];
    var followerStrategies = ["(F-A)", "(F-B)", "(F-C)", "(F-D)"];

    while (hostIndex < amountOfPlayers - 1) {
        for (var follower = 0; follower <= 3; follower++) {
            for (var accept = 0; accept <= acceptLimit; accept++) {
                neighbours[hostIndex].strategy = leaderStrategies[follower] + " | " + followerStrategies[accept];
                neighbours[hostIndex++].connection.emit('control', {
                    type: 'init',
                    amountOfPlayers: amountOfPlayers,
                    data: {strategy: 'both', leader: leader - follower, follower: follower, accept: accept}
                });
            }
        }
    }
}

/** Initialisiere Knoten für A4 */
function initA4() {
    resetNodes();
    var p = 8;
    var leader = p;
    var acceptLimit = p;
    var hostIndex = 0;

    while (hostIndex < neighbours.length - 1) {
        for (var follower = 0; follower <= 3; follower++) {
            for (var accept = 0; accept <= acceptLimit; accept++) {
                if (hostIndex == neighbours.length - 1) {
                    break;
                }
                neighbours[hostIndex++].connection.emit('control', {
                    type: 'init',
                    amountOfPlayers: neighbours.length,
                    data: {strategy: 'both', leader: leader - follower, follower: follower, accept: accept}
                });
            }
        }
    }
}

/** Initialisiere Knoten für A5 */
function initA5() {
    resetNodes();
    var moneyLimit = 100;
    var leader = 3;
    var acceptLimit = 3;
    var hostIndex = 0;

    var leaderStrategies = ["(L-A)", "(L-B)", "(L-C)", "(L-D)"];
    var followerStrategies = ["(F-A)", "(F-B)", "(F-C)", "(F-D)"];

    while (hostIndex < neighbours.length - 1) {
        for (var follower = 0; follower <= 3; follower++) {
            for (var accept = 0; accept <= acceptLimit; accept++) {
                neighbours[hostIndex].strategy = leaderStrategies[follower] + " | " + followerStrategies[accept];
                neighbours[hostIndex++].connection.emit('control', {
                    type: 'init',
                    amountOfPlayers: neighbours.length,
                    moneyLimit: moneyLimit,
                    data: {strategy: 'both', leader: leader - follower, follower: follower, accept: accept}
                });
            }
        }
    }
}

/** Setzt die Stratgie der Knoten zurck(nur Anzeige) */
function resetStrategy() {
    neighbours.forEach(function (neighbour) {
        neighbour.strategy = '';
    });
    nodeWithMostMoney = null;
}

/** Doppelzählverfahren für die Terminierung festzustellen */
function startDoubleCountCheck() {
    doubleCheckCounter = 0;
    doubleCheckNodeResponseCounter = 1;
    neighbours.forEach(function (neighbour) {
        neighbour.connection.emit('control', {type: 'doubleCountCheck'});
    });
}

/** Erfrage Geld der Knoten */
function requestMoney() {
    answerCounter = 1;
    neighbours.forEach(function (neighbour) {
        neighbour.connection.emit('control', {type: "requestMoney"});
    });
}

/** Setze Spielparameter der Knoten zurück */
function resetNodes(){
    neighbours.forEach(function(neighbour){
        neighbour.connection.emit('control', {type: 'reset'});
    });
    resetStrategy();
}