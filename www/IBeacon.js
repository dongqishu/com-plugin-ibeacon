var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'IBeacon', 'coolMethod', [arg0]);
};
exports.startListening = function (arg0, success, error) {
    exec(success, error, 'IBeacon', 'startListening', [arg0]);
};
exports.stopListening = function (arg0, success, error) {
    exec(success, error, 'IBeacon', 'stopListening', [arg0]);
};