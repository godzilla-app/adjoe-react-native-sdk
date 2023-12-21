import { NativeModules } from 'react-native';

const { RNAdjoeSdk } = NativeModules;

export default {
  // functions
  initialize: function(appHash) {
    return RNAdjoeSdk.pvInitialize(appHash);
  },

  setAppHash: function(appHash) {
    return RNAdjoeSdk.pvSetAppHash(appHash);
  },

  setCheckCallback: function() {
    return RNAdjoeSdk.pvSetCheckCallback();
  },

  setVerifyCallback: function() {
    return RNAdjoeSdk.pvSetVerifyCallback();
  },

  getStatus: function() {
    return RNAdjoeSdk.pvGetStatus();
  },

  startManual: function(phoneNumber) {
    return RNAdjoeSdk.pvStartManual(phoneNumber);
  },

  verifyCode: function(code) {
    return RNAdjoeSdk.pvVerifyCode(code);
  },

  startAutomatic: function() {
    return RNAdjoeSdk.pvStartAutomatic();
  },

  startAutomaticWithPhoneNumber: function(phoneNumber) {
    return RNAdjoeSdk.pvStartAutomaticWithPhoneNumber(phoneNumber);
  },
};
