//server.js

const express = require('express');
const app = express();
const server = require('http').createServer(app);

app.use(express.static('knoparking'));

server.on("connection", function(connection) {
    console.log("new connection to server");
});

server.listen(55555);
