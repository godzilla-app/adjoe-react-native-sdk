import { NativeModules } from 'react-native';

const { RNPlaytimeSdk } = NativeModules;

const getPlaytimeParams = (params, uaChannel) => {
  // if first params is an object then proceed as playtimeParams
  // otherwise first params is uaNetwork
  var playtimeParams = {};
  if (typeof params === "object" && params !== null) {
    playtimeParams = params;
  } else if (typeof params === "string") {
    playtimeParams["uaNetwork"] = params;
    if (uaChannel !== null) {
      playtimeParams["uaChannel"] = uaChannel;
    }
  }
  return playtimeParams;
};

export default {
  EVENT_TEASER_SHOWN: RNPlaytimeSdk.EVENT_TEASER_SHOWN,
  
  // functions
  init: function(apiKey, options = null, uaNetwork = null, uaChannel = null) {
    if(options) {
      options.playtimeParams = options.playtimeParams || {};
      if(uaNetwork != null) {
        options.playtimeParams["uaNetwork"] = uaNetwork;
      }
      if (uaChannel != null) {
        options.playtimeParams["uaChannel"] = uaChannel;
      }
    }
    return RNPlaytimeSdk.init(apiKey, options);
  },

  showCatalog: function(params = null, uaChannel = null) {
    var playtimeParams = getPlaytimeParams(params, uaChannel);
    return RNPlaytimeSdk.showCatalog(playtimeParams);
  },

  showCatalogWithOptions: function(options) {
    return RNPlaytimeSdk.showCatalogWithOptions(options);
  },

  requestRewards: function(params = null, uaChannel = null) {
    var playtimeParams = getPlaytimeParams(params, uaChannel);
    return RNPlaytimeSdk.requestRewards(playtimeParams);
  },

  doPayout: function(params = null, uaChannel = null) {
    var playtimeParams = getPlaytimeParams(params, uaChannel);
    return RNPlaytimeSdk.doPayout(playtimeParams);
  },

  setUAParams: function (params) {
    return RNPlaytimeSdk.setUAParams(params);
  },

  getVersion: function() {
    return RNPlaytimeSdk.getVersion();
  },

  getVersionName: function() {
    return RNPlaytimeSdk.getVersionName();
  },

  isInitialized: function() {
    return RNPlaytimeSdk.isInitialized();
  },

  hasAcceptedTOS: function() {
    return RNPlaytimeSdk.hasAcceptedTOS();
  },

  hasAcceptedUsagePermission: function() {
    return RNPlaytimeSdk.hasAcceptedUsagePermission();
  },

  getUserId: function() {
    return RNPlaytimeSdk.getUserId();
  },

  sendEvent: function (event, extra = null, params = null, uaChannel = null) {
    var playtimeParams = getPlaytimeParams(params, uaChannel);
    RNPlaytimeSdk.sendEvent(event, extra, playtimeParams);
},
};
