import { AdjoeParams } from './AdjoeParams';
declare namespace _default {
    function init(apiKey: string, options: {user_id: string; params: AdjoeParams;}, uaNetwork?: string, uaChannel?: string): void;
    function showOfferwall(params?: AdjoeParams, uaChannel?: string): void;
    function canShowOfferwall(): boolean;
    function canShowPostInstallRewardOfferwall(): boolean;
    function requestRewards(params?: AdjoeParams, uaChannel?: string): void;
    function doPayout(params?: AdjoeParams, uaChannel?: string): void;
    function setProfile(source: string, gender: string, birthday: string, params?: AdjoeParams, uaChannel?: string): void;
    function setUAParams(params: AdjoeParams): void;
    function getVersion(): number;
    function getVersionName(): string;
    function isInitialized(): boolean;
    function hasAcceptedTOS(): boolean;
    function hasAcceptedUsagePermission(): boolean;
    function getUserId(): string;
    function canUseOfferwallFeatures(): boolean;
    function _a(b: boolean): void;
    function faceVerification(): void;
    function faceVerificationStatus(): null;
}
export default _default;
//# sourceMappingURL=Adjoe.d.ts.map
