import { PlaytimeOptions } from './PlaytimeOptions';
export interface PlaytimeRewardAction {
    name: string;
    taskDescription: string;
    taskType: string;
    playDuration?: number | null;
    level?: number | null;
    amount: number;
    rewardedAt?: string | null;
    rewardsCount?: number | null;
    completedRewards?: number | null;
    timedCoinsDuration?: number | null;
    timedCoins?: number | null;
    originalCoins?: number | null;
    isTimed?: boolean | null;
    isRewardedForPromotion?: boolean | null;
    boosterExpiresAt?: string | null;
}

export interface PlaytimePromotion {
    name?: string | null;
    promotionDescription?: string | null;
    boostFactor?: number | null;
    startTime?: string | null;
    endTime?: string | null;
    targetingType?: string | null;
}

export interface PlaytimeMedia {
    portrait?: string | null;
    landscape?: string | null;
}

export interface PlaytimeEventConfig {
    sequentialActions: PlaytimeRewardAction[];
    bonusActions: PlaytimeRewardAction[];
    timeBasedActions: PlaytimeRewardAction[];
    totalCoinsCollected: number;
    totalCoinsPossible?: number | null;
    cashbackReward?: PlaytimeCashbackConfig | null;
    multipliersActions: PlaytimeRewardActionMultiplier[];
}

export interface PlaytimeCashbackConfig {
    exchangeRate?: number | null;
    cashbackDescription?: string | null;
    maxLimitPerCampaignUSD?: number | null;
    maxLimitPerCampaignCoins?: number | null;
    completedRewards?: PlaytimeCashbackReward | null;
    pendingRewards?: PlaytimeCashbackReward | null;
}

export interface PlaytimeCashbackReward {
    totalCoins?: number | null;
    events?: PlaytimeCashbackRewardEvent[] | null;
}

export interface PlaytimeCashbackRewardEvent {
    coins?: number | null;
    processAt?: string | null;
    receivedAt?: string | null
}

export interface PlaytimeRewardActionMultiplier {
    eventName?: string | null;
    eventDescription?: string | null;
    multiplierFactorPercentage?: number | null;
    multiplierLevels?: number | null;
    status?: string | null;
    usedLevels?: number | null;
}

export interface PlaytimeCampaign {
    campaignUUID?: string | null;
    appName: string;
    appDescription: string;
    appID: string;
    appBundleID?: string | null;
    appStoreID?: string | null;
    installedAt?: string | null;
    uninstalledAt?: string | null;
    rewardingExpiresAfter?: number | null;
    rewardingExpiresAt?: string | null;
    campaignExpiresAt?: string | null;
    appCategory?: string | null;
    campaignType?: string | null;
    featuredPosition?: number | null;
    score?: number | null;
    iconImage?: string | null;
    image?: PlaytimeMedia | null;
    video?: PlaytimeMedia | null;
    promotion?: PlaytimePromotion | null;
    eventConfig?: PlaytimeEventConfig | null;
    isCompleted?: boolean | null;
}

export interface PlaytimeCampaignsResponse {
    campaigns: PlaytimeCampaign[];
}

export interface PlaytimeCampaignsResponse {
    campaigns: PlaytimeCampaign[];
}

export interface PlaytimePermissions {
    isTOSAccepted: boolean;
    isUsagePermissionAccepted: boolean;
}

export interface PlaytimePermissionsResponse {
    permissions: PlaytimePermissions
}

declare namespace _default {
    function getCampaigns(options: PlaytimeOptions): Promise<PlaytimeCampaignsResponse>;

    function getInstalledCampaigns(options: PlaytimeOptions): Promise<PlaytimeCampaignsResponse>;

    function openInStore(campaign: PlaytimeCampaign): Promise<void>;

    function openInstalledCampaign(campaign: PlaytimeCampaign): Promise<void>;

    function getPermissions(): Promise<PlaytimePermissionsResponse>;

    function showPermissionsPrompt(): Promise<PlaytimePermissionsResponse>;
}

export default _default;