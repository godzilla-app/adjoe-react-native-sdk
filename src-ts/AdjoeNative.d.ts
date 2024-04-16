import { AdjoeParams } from './AdjoeParams';
declare namespace _default {
    let VERSION: number;
    let PAYOUT_ALL_COINS: number;
    let PAYOUT_NOT_ENOUGH_COINS: number;
    let PAYOUT_TOS_NOT_ACCEPTED: number;
    let PAYOUT_UNKNOWN: number;
    let EVENT_AGB_SHOWN: number;
    let EVENT_AGB_ACCEPTED: number;
    let EVENT_AGB_DECLINED: number;
    let EVENT_USAGE_PERMISSION_ACCEPTED: number;
    let EVENT_USAGE_PERMISSION_DENIED: number;
    let EVENT_INSTALL_CLICKED: number;
    let EVENT_VIDEO_PLAY: number;
    let EVENT_VIDEO_PAUSE: number;
    let EVENT_VIDEO_ENDED: number;
    let EVENT_CAMPAIGNS_SHOWN: number;
    let EVENT_CAMPAIGN_VIEW: number;
    let EVENT_APP_OPEN: number;
    let EVENT_FIRST_IMPRESSION: number;
    let EVENT_TEASER_SHOWN: number;
    function requestPartnerApps(params?: AdjoeParams, uaChannel?: string): Promise<any>;
    function requestCampaignApps(params?: AdjoeParams, uaChannel?: string): Promise<any>;
    function requestInstalledPartnerApps(params?: AdjoeParams, uaChannel?: string): Promise<any>;
    function requestPostInstallRewardCampaignApps(params?: AdjoeParams, uaChannel?: string): Promise<any>;
    function setTosAccepted(): void;
    function setUsagePermissionAccepted(): void;
    function requestUsagePermission(bringBackAfterAccept?: boolean, ticks?: number): void;
    function sendEvent(event: number, extra: string, params?: AdjoeParams, uaChannel?: string): void;
}
export default _default;
//# sourceMappingURL=AdjoeNative.d.ts.map