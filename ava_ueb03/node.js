/** Einlesen der ID des gestarteten Knoten */
var id = process.argv[2];
if(id == undefined){
    console.log("Param 1: ID required");
    return 1;
}
fs = require('fs');
/** Laden der Hostdatei */
var hostslist = require('./hostlist.json');
var file = './accessFile.txt';

/**Node Variabeln */
var node;
var neighbours = [];
var queue = new Queue();
var readZeroesCounter = 0;
var acknowledgesPending = {};

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
    socket.on('request', function (msg) {
        queue.push(msg.ts, msg.senderId);
        socket.emit('acknowledgement', {ts: msg.ts});
        //queue.log();
    });
    socket.on('release', function (msg){
        queue.remove(msg.ts, msg.senderId);
        //queue.log();
        checkCriticalSection();
    });
});

/** Lade Clientsocket-packet */
var ioc = require( 'socket.io-client' );

/** Verbinden der Clients */
neighbours.forEach(function (neighbour){
    neighbour.connection = ioc.connect("http://"+neighbour.hostname+ ":" + neighbour.port);
    neighbour.connection.on('acknowledgement', function(msg){
        acknowledgesPending[msg.ts]--;
        checkCriticalSection(msg.ts);
    });
});



function requestCriticalSection(){
    var ts = new Date().getTime();
    acknowledgesPending[ts] = neighbours.length;
    queue.push(ts, node.id);
    neighbours.forEach(function(neighbour){
       //console.log('Request critical Section to '+neighbour.id);
       neighbour.connection.emit('request', {ts: ts, senderId: node.id});
   });
}

function exitCriticalSection(ts){
    queue.remove(ts, node.id);
    neighbours.forEach(function(neighbour){
        neighbour.connection.emit('release', {ts: ts, senderId: node.id});
    });
}

/** Implementierung der Queue für den Lamport-Algorithmus */
function Queue(){
    var lArray = [];
    this.push = function(timestamp, id){
        lArray.push({ts: timestamp, id: id});
        lArray.sort(function(a, b){
            // Sort by timestamp(1) and id(2)
            return  (a.ts - b.ts || a.id - b.id);
        });
    }
    this.pop = function(){
        return lArray.shift();
    }
    this.remove = function(timestamp, id){
        for(var i = 0; i < lArray.length; i++){
            if(lArray[i].id == id && lArray[i].ts == timestamp){
                lArray.splice(i, 1);
                break;
            }
        }
    }
    this.first = function(){
        if(lArray.length < 1){
            //console.log('Queue empty')
            return false;
        }else{
            return lArray[0];
        }
    }
    this.containsAllNeighbours = function(neighbours){
        var containsAll = true;
        for(var i = 0; i < neighbours.length; i++){
            var inQueue = false;
            for(var j = 0; i < lArray.length; j++){
                if(lArray[i].id == lArray[j].id){
                    inQueue = true;
                    break;
                }
            }
            if(!inQueue){
                containsAll = false;
                break;
            }
        }
        return containsAll;
    }
    this.log = function(){
        console.log(lArray);
    }
}

function enterCriticalSection(ts){
    var fileContents = fs.readFileSync(file).toString().split('\n')
    var number = fileContents[0];
    if(number == 0){
        readZeroesCounter++;
    }
    if(node.id % 2 == 0){
        number--;
    }else{
        number++;
    }
    fileContents[0] = number;
    fileContents.push(node.id);
    fs.writeFileSync(file, fileContents.join('\n'));
    exitCriticalSection(ts);
    if(readZeroesCounter >= 3){
        console.log('Done');
    }else{
        setTimeout(requestCriticalSection, randomWaitTime());
    }
}

function checkCriticalSection(){
    var firstQueueObj = queue.first();
    if(firstQueueObj != false && firstQueueObj.id == node.id && acknowledgesPending[firstQueueObj.ts] == 0){
        enterCriticalSection(firstQueueObj.ts);
    }
}

function randomWaitTime(){
    return Math.floor(Math.random()*(501));
}

requestCriticalSection();