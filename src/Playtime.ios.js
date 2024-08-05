export default {
  // functions
  init: function(apiKey, options, uaNetwork = null, uaChannel = null) {
    return this.showError();
  },

  showCatalog: function(params = null, uaChannel = null) {
    return this.showError();
  },

  requestRewards: function(params = null, uaChannel = null) {
    return this.showError();
  },

  setUAParams: function (params) {
    return this.showError();
  },

  getVersion: function() {
    return this.showError();
  },

  getVersionName: function() {
    return this.showError();
  },

  isInitialized: function() {
    return Promise.resolve(false);
  },

  hasAcceptedTOS: function() {
    return Promise.resolve(false);
  },

  hasAcceptedUsagePermission: function() {
    return Promise.resolve(false);
  },

  getUserId: function() {
    return this.showError();
  },

  _a: function(b) {
     return this.showError();
  },

  faceVerification: function() {
    return this.showError();
  },
  
  erfanMethod: function() {
    return this.showError();
  },

  faceVerificationStatus: function() {
    return this.showError();
  },

  showError: async function () {
          console.log("Playtime is not available on iOS");
          return new Promise(() => {});
      }
};
