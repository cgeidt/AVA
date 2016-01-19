fs = require('fs');
var hostlist = [];
var port = 50000;
for(i = 1; i <= 48; i++){
    hostlist.push({id: i, hostname: "127.0.0.1", port: port++});
}
fs.writeFile('hostlist.json', JSON.stringify(hostlist, null, 4));