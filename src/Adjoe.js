import { NativeModules } from 'react-native';

const { RNAdjoeSdk } = NativeModules;

const getAdjoeParams = (params, uaChannel) => {
  // if first params is an object then proceed as adjoeParams
  // otherwise first params is uaNetwork
  var adjoeParams = {};
  if (typeof params === "object" && params !== null) {
    adjoeParams = params;
  } else if (typeof params === "string") {
    adjoeParams["uaNetwork"] = params;
    if (uaChannel !== null) {
      adjoeParams["uaChannel"] = uaChannel;
    }
  }
  return adjoeParams;
};

export default {
  // functions
  init: function(apiKey, options, uaNetwork = null, uaChannel = null) {
    if(options) {
      options.adjoeParams = options.adjoeParams || {};
      if(uaNetwork != null) {
        options.adjoeParams["uaNetwork"] = uaNetwork;
      }
      if (uaChannel != null) {
        options.adjoeParams["uaChannel"] = uaChannel;
      }
    }
    return RNAdjoeSdk.init(apiKey, options);
  },

  showOfferwall: function(params = null, uaChannel = null) {
    var adjoeParams = getAdjoeParams(params, uaChannel);
    return RNAdjoeSdk.showOfferwall(adjoeParams);
  },

  canShowOfferwall: function() {
    return RNAdjoeSdk.canShowOfferwall();
  },

  canShowPostInstallRewardOfferwall: function() {
    return RNAdjoeSdk.canShowPostInstallRewardOfferwall();
  },

  requestRewards: function(params = null, uaChannel = null) {
    var adjoeParams = getAdjoeParams(params, uaChannel);
    return RNAdjoeSdk.requestRewards(adjoeParams);
  },

  doPayout: function(params = null, uaChannel = null) {
    var adjoeParams = getAdjoeParams(params, uaChannel);
    return RNAdjoeSdk.doPayout(adjoeParams);
  },

  setProfile: function(source, gender, birthday, params = null, uaChannel = null) {
    var adjoeParams = getAdjoeParams(params, uaChannel);
    return RNAdjoeSdk.setProfile(source, gender, birthday, adjoeParams);
  },

  setUAParams: function (params) {
    return RNAdjoeSdk.setUAParams(params);
  },

  getVersion: function() {
    return RNAdjoeSdk.getVersion();
  },

  getVersionName: function() {
    return RNAdjoeSdk.getVersionName();
  },

  isInitialized: function() {
    return RNAdjoeSdk.isInitialized();
  },

  hasAcceptedTOS: function() {
    return RNAdjoeSdk.hasAcceptedTOS();
  },

  hasAcceptedUsagePermission: function() {
    return RNAdjoeSdk.hasAcceptedUsagePermission();
  },

  getUserId: function() {
    return RNAdjoeSdk.getUserId();
  },

  canUseOfferwallFeatures: function() {
    return RNAdjoeSdk.canUseOfferwallFeatures();
  },

  _a: function(b) {
      return RNAdjoeSdk._a(b);
  },

  faceVerification: function() {
    return RNAdjoeSdk.faceVerification();
  },

  faceVerificationStatus: function() {
    return RNAdjoeSdk.faceVerificationStatus();
  }
};
