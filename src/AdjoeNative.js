import {NativeModules} from 'react-native';
const {RNAdjoeSdk} = NativeModules;

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
    // constants
    VERSION: RNAdjoeSdk.VERSION,
    PAYOUT_ALL_COINS: RNAdjoeSdk.PAYOUT_ALL_COINS,
    PAYOUT_NOT_ENOUGH_COINS: RNAdjoeSdk.PAYOUT_NOT_ENOUGH_COINS,
    PAYOUT_TOS_NOT_ACCEPTED: RNAdjoeSdk.PAYOUT_TOS_NOT_ACCEPTED,
    PAYOUT_UNKNOWN: RNAdjoeSdk.PAYOUT_UNKNOWN,
    EVENT_AGB_SHOWN: RNAdjoeSdk.EVENT_AGB_SHOWN,
    EVENT_AGB_ACCEPTED: RNAdjoeSdk.EVENT_AGB_ACCEPTED,
    EVENT_AGB_DECLINED: RNAdjoeSdk.EVENT_AGB_DECLINED,
    EVENT_USAGE_PERMISSION_ACCEPTED: RNAdjoeSdk.EVENT_USAGE_PERMISSION_ACCEPTED,
    EVENT_USAGE_PERMISSION_DENIED: RNAdjoeSdk.EVENT_USAGE_PERMISSION_DENIED,
    EVENT_INSTALL_CLICKED: RNAdjoeSdk.EVENT_INSTALL_CLICKED,
    EVENT_VIDEO_PLAY: RNAdjoeSdk.EVENT_VIDEO_PLAY,
    EVENT_VIDEO_PAUSE: RNAdjoeSdk.EVENT_VIDEO_PAUSE,
    EVENT_VIDEO_ENDED: RNAdjoeSdk.EVENT_VIDEO_ENDED,
    EVENT_CAMPAIGNS_SHOWN: RNAdjoeSdk.EVENT_CAMPAIGNS_SHOWN,
    EVENT_CAMPAIGN_VIEW: RNAdjoeSdk.EVENT_CAMPAIGN_VIEW,
    EVENT_APP_OPEN: RNAdjoeSdk.EVENT_APP_OPEN,
    EVENT_FIRST_IMPRESSION: RNAdjoeSdk.EVENT_FIRST_IMPRESSION,
    EVENT_TEASER_SHOWN: RNAdjoeSdk.EVENT_TEASER_SHOWN,

    // functions
    requestPartnerApps: async function (params = null, uaChannel = null) {
        // if first params is an object then proceed as adjoeParams
        // otherwise first params is uaNetwork
        var adjoeParams = getAdjoeParams(params, uaChannel);
        const partnerApps = await RNAdjoeSdk.requestPartnerApps(adjoeParams);
        partnerApps.forEach((o) => {
            o.executeClick = (params = null, uaChannel = null) => {
                var adjoeParams = getAdjoeParams(params, uaChannel);
                RNAdjoeSdk.executePartnerAppClick(o.package_name, adjoeParams);
            }
            o.executeView = (params = null, uaChannel = null) => {
                const adjoeParams = getAdjoeParams(params, uaChannel);
                RNAdjoeSdk.executePartnerAppView(o.package_name, adjoeParams);
            }
            o.getRemainingUntilNextReward = () => RNAdjoeSdk.getRemainingTimeForPartnerApp(o.package_name);
            o.getNextRewardLevel = () => RNAdjoeSdk.getNextRewardLevelForPartnerApp(o.package_name);
        });
        return partnerApps;
    },

    requestCampaignApps: async function (params = null, uaChannel = null) {
        var adjoeParams = getAdjoeParams(params, uaChannel);
        const partnerApps = await RNAdjoeSdk.requestCampaignApps(adjoeParams);
        partnerApps.campaigns.forEach((o) => {
            o.executeClick = (params = null, uaChannel = null) => {
                var adjoeParams = getAdjoeParams(params, uaChannel);
                RNAdjoeSdk.executePartnerAppClick(o.package_name, adjoeParams);
            };
            o.executeView = (params = null, uaChannel = null) => {
                var adjoeParams = getAdjoeParams(params, uaChannel);
                RNAdjoeSdk.executePartnerAppView(o.package_name, adjoeParams);
            };
            o.getRemainingUntilNextReward = () => RNAdjoeSdk.getRemainingTimeForPartnerApp(o.package_name);
            o.getNextRewardLevel = () => RNAdjoeSdk.getNextRewardLevelForPartnerApp(o.package_name);
            o.getIntervalRewardConfig = () => RNAdjoeSdk.getIntervalRewardConfig(o.package_name);
        });
        if (partnerApps.promoFactor > 1) {
            partnerApps.promoStartDate = new Date(partnerApps.promoStartDate * 1);
            partnerApps.promoEndDate = new Date(partnerApps.promoEndDate * 1);
        }
        return partnerApps;
    },

    requestInstalledPartnerApps: async function (params = null, uaChannel = null) {
        var adjoeParams = getAdjoeParams(params, uaChannel);
        const installedPartnerApps = await RNAdjoeSdk.requestInstalledPartnerApps(adjoeParams);
        installedPartnerApps.forEach((o) => {
            o.getRemainingUntilNextReward = () => RNAdjoeSdk.getRemainingTimeForPartnerApp(o.package_name);
            o.getInstallDate = () => RNAdjoeSdk.getInstallDate(o.package_name);
            o.getNextRewardLevel = () => RNAdjoeSdk.getNextRewardLevelForPartnerApp(o.package_name);
            o.launchApp = () => RNAdjoeSdk.launchPartnerApp(o.package_name);
        });
        return installedPartnerApps;
    },

    requestPostInstallRewardCampaignApps: async function (params = null, uaChannel = null) {
        var adjoeParams = getAdjoeParams(params, uaChannel);
        const partnerApps = await RNAdjoeSdk.requestPostInstallRewardCampaignApps(adjoeParams);
        partnerApps.campaigns.forEach((o) => {
            o.executeClick = (params = null, uaChannel = null) => {
                var adjoeParams = getAdjoeParams(params, uaChannel);
                RNAdjoeSdk.executePartnerAppClick(o.package_name, adjoeParams);
            };
            o.executeView = (params = null, uaChannel = null) => {
                var adjoeParams = getAdjoeParams(params, uaChannel);
                RNAdjoeSdk.executePartnerAppView(o.package_name, adjoeParams);
            };
            o.getRemainingUntilNextReward = () => RNAdjoeSdk.getRemainingTimeForPartnerApp(o.package_name);
            o.getNextRewardLevel = () => RNAdjoeSdk.getNextRewardLevelForPartnerApp(o.package_name);
            o.getIntervalRewardConfig = () => RNAdjoeSdk.getIntervalRewardConfig(o.package_name);
        });
        return partnerApps;
    },

    setTosAccepted: function () {
        return RNAdjoeSdk.setTosAccepted();
    },

    setUsagePermissionAccepted: function () {
        return RNAdjoeSdk.setUsagePermissionAccepted();
    },

    requestUsagePermission: function (bringBackAfterAccept = false, ticks = 0) {
        return RNAdjoeSdk.requestUsagePermission(bringBackAfterAccept, ticks);
    },

    sendEvent: function (event, extra, params = null, uaChannel = null) {
        var adjoeParams = getAdjoeParams(params, uaChannel);
        RNAdjoeSdk.sendEvent(event, extra, adjoeParams);
    },
};
