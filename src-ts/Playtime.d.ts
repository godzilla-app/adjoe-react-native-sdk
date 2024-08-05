import { PlaytimeParams } from './PlaytimeParams';
import { PlaytimeExtension } from './PlaytimeExtension';
import { PlaytimeUserProfile } from './PlaytimeUserProfile';
import { PlaytimeReward } from './PlaytimeReward';

declare namespace _default {
    function init(apiKey: string, options: {
        user_id: string;
        playtimeParams: PlaytimeParams;
        applicationProcessName: string;
        playtimeExtension: PlaytimeExtension;
        playtimeUserProfile: PlaytimeUserProfile;
    }, uaNetwork?: string, uaChannel?: string): Promise<void>;
    function showCatalog(params?: PlaytimeParams, uaChannel?: string): Promise<void>;
    function requestRewards(params?: PlaytimeParams, uaChannel?: string): Promise<PlaytimeReward>;
    function doPayout(params?: PlaytimeParams, uaChannel?: string): Promise<number>;
    function setUAParams(params: PlaytimeParams): Promise<void>;
    function getVersion(): Promise<string>;
    function getVersionName(): Promise<string>;
    function isInitialized(): Promise<boolean>;
    function hasAcceptedTOS(): Promise<boolean>;
    function hasAcceptedUsagePermission(): Promise<boolean>;
    function getUserId(): Promise<string>;
    function _a(b: any): Promise<void>;
    function faceVerification(): Promise<void>;
    function faceVerificationStatus(): Promise<void>;
}
export default _default;
//# sourceMappingURL=Playtime.d.ts.map