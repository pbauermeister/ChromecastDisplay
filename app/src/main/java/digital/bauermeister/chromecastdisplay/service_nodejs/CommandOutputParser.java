package digital.bauermeister.chromecastdisplay.service_nodejs;

/**
 * Created by pascal on 3/9/16.
 */
public class CommandOutputParser {
/*
    [
    {"event":"DISCOVERING"},
    {"event":"DISCOVERED","device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}}},
    {"event":"CONNECTED","device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}}},
    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3597,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"GET_STATUS","request":{"name":"Chromeister","nbSent":0,"nbReceived":1}},
    {"event":"ERROR","error":{"code":"ENODEV","errno":"ENODEV","syscall":"addMembership"}},

    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3598,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"GET_STATUS","request":{"name":"Chromeister","nbSent":1,"nbReceived":2}},
    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3599,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"GET_STATUS","request":{"name":"Chromeister","nbSent":2,"nbReceived":3}},
    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3600,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"DISCOVERING"},
    {"event":"GET_STATUS","request":{"name":"Chromeister","nbSent":3,"nbReceived":4}},
    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3601,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"GET_STATUS","request":{"name":"Chromeister","nbSent":4,"nbReceived":5}},
    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3602,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"GET_STATUS","request":{"name":"Chromeister","nbSent":5,"nbReceived":6}},
    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3603,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"GET_STATUS","request":{"name":"Chromeister","nbSent":6,"nbReceived":7}},
    {"event":"STATUS","status":{"device":{"location":"http://20.0.0.6:8008/ssdp/device-desc.xml","applicationUrl":"http://20.0.0.6:8008/apps/","name":"Chromeister","model":"Eureka Dongle","info":{"deviceType":"urn:dial-multiscreen-org:device:dial:1","friendlyName":"Chromeister","manufacturer":"Google Inc.","modelName":"Eureka Dongle","UDN":"uuid:80525115-3215-11ae-9977-01a44d51d43f","iconList":{"icon":{"mimetype":"image/png","width":98,"height":55,"depth":32,"url":"/setup/icon.png"}},"serviceList":{"service":{"serviceType":"urn:dial-multiscreen-org:service:dial:1","serviceId":"urn:dial-multiscreen-org:serviceId:dial","controlURL":"/ssdp/notfound","eventSubURL":"/ssdp/notfound","SCPDURL":"http://www.google.com/cast"}}}},"data":{"requestId":3604,"status":{"applications":[{"appId":"E8C28D3C","displayName":"Backdrop","namespaces":[{"name":"urn:x-cast:com.google.cast.sse"}],"sessionId":"FB043167-26D4-491B-9DA8-E5B1CDA6E1B6","statusText":"","transportId":"web-0"}],"volume":{"level":1,"muted":false}},"type":"RECEIVER_STATUS"}}},
    {"event":"DISCOVERING"},
    null]
    */
}
