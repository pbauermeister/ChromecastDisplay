#!/usr/bin/nodejs

// To split urls:
var url = require('url');

// For Chromecast discovery w/o Avahi:
// (see https://www.npmjs.com/package/chromecast)
var chromecast = require('chromecast')();

// For all other Chromecast operations:
// (see https://www.npmjs.com/package/castv2)
var Client = require('castv2').Client;

var POLL_DELAY_MS = 5 * 1000;
var DISCOVERY_DELAY_MS = 20 * 1000;

///////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////

function out(kind, value) {
    var output = {};
    output[kind] = value;
    console.log(JSON.stringify(output));
}

///////////////////////////////////////////////////////////////////////////////
// DEVICE MANAGEMENT
// - At each discovery we may re-discover a device that we already follow.
// - The following code helps making sure that we have only one instance
//   per host.
///////////////////////////////////////////////////////////////////////////////

var instances = {};
var refNr = 0;
function checkInstance(host, ref) {
    if (!instances[host]) instances[host] = {};
    instances[host][ref] = true; // add us
    if (Object.keys(instances[host]).length>1) {
	// another instance => remove us and leave the other
	delete instances[host][ref];
	return false;
    }
    return true; // ok, we're the only one
}

function hasInstance(host, ref) {
    if (!instances[host]) return false;
    if (!instances[host][ref]) return false;
    return true;
}

function removeInstance(host, ref) {
    if (!instances[host]) return;
    delete instances[host][ref];
    if(Object.keys(instances[host]).length<1)
	delete instances[host];
}


///////////////////////////////////////////////////////////////////////////////
// Handler for discovered hosts
///////////////////////////////////////////////////////////////////////////////

chromecast.on('device', function(device){
    var host = url.parse(device.location).hostname;
    var ref = ++refNr;
    if (!checkInstance(host, ref)) return;
    var name = device.name;
    var xname = name + "@" + ref;

    out("DISCOVERED", device);
    var seq = (new Date).getTime() % 10000;
    var client = new Client();

    client.connect(host, function() {

	///////////////////////////////////////////////////////////////////////
	// Handler for connected device
	///////////////////////////////////////////////////////////////////////

	out("CONNECTED", device);
	var nbSent = 0;
	var nbReceived = 0;

	if (!checkInstance(host, ref)) return;

	// create various namespace handlers 
	var connection = client.createChannel('sender-0', 'receiver-0', 'urn:x-cast:com.google.cast.tp.connection', 'JSON');
	var heartbeat  = client.createChannel('sender-0', 'receiver-0', 'urn:x-cast:com.google.cast.tp.heartbeat', 'JSON');
	var receiver   = client.createChannel('sender-0', 'receiver-0', 'urn:x-cast:com.google.cast.receiver', 'JSON');
 
	// establish virtual connection to the receiver 
	connection.send({ type: 'CONNECT' });
 
	// start heartbeating
	var iid = setInterval(function() {
	    // remove non-responding
	    if (nbSent>8 && nbSent/2>nbReceived)
		removeInstance(host, ref);

	    if (!hasInstance(host, ref)) {
		// dead or deleted instance
		connection.close();
		heartbeat.close();
		receiver.close();
		client.close();
		clearInterval(iid);
	    }
	    else {
		// ping
	        out("GET_STATUS", name + ": " + nbSent + ">" + nbReceived);
		++nbSent;
	        receiver.send({ "type": "GET_STATUS", "requestId": ++seq });
	    }
	}, POLL_DELAY_MS);

	// display receiver status updates 
	receiver.on('message', function(data, broadcast) {
	    if(data.type = 'RECEIVER_STATUS') {
		++nbReceived;
		var status = { "device": device, "data": data };
		out("STATUS", status);
	    }
	});

	// initial status
	setTimeout(function() {
	    if (hasInstance(host, ref))
		receiver.send({ "type": "GET_STATUS", "requestId": ++seq });
	}, 2000);
    });
});

// initial discover
out("DISCOVERING", null);
chromecast.discover();

// restart on error
process.on('uncaughtException', function (err) {
    out("ERROR", err);
    console.error(err.stack);
    instances = {};
    out("DISCOVERING", null);
    chromecast.discover();
});

// periodic re-discover
setInterval(function() {
    out("DISCOVERING", null);
    chromecast.discover();
}, DISCOVERY_DELAY_MS);
