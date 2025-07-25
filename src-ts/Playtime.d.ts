import { PlaytimeParams } from './PlaytimeParams';
import { PlaytimeExtension } from './PlaytimeExtension';
import { PlaytimeUserProfile } from './PlaytimeUserProfile';
import { PlaytimeReward } from './PlaytimeReward';
import { PlaytimeOptions } from './PlaytimeOptions';

declare namespace _default {
    let EVENT_TEASER_SHOWN: number;
    function init(apiKey: string, options?: {
        userId?: string;
        playtimeParams?: PlaytimeParams;
        applicationProcessName?: string;
        playtimeExtension?: PlaytimeExtension;
        playtimeUserProfile?: PlaytimeUserProfile;
    }, uaNetwork?: string, uaChannel?: string): Promise<void>;
    function showCatalog(params?: PlaytimeParams, uaChannel?: string): Promise<void>;
    function showCatalogWithOptions(options: PlaytimeOptions): Promise<void>;
    function requestRewards(params?: PlaytimeParams, uaChannel?: string): Promise<PlaytimeReward>;
    function doPayout(params?: PlaytimeParams, uaChannel?: string): Promise<number>;
    function setUAParams(params: PlaytimeParams): Promise<void>;
    function getVersion(): Promise<string>;
    function getVersionName(): Promise<string>;
    function isInitialized(): Promise<boolean>;
    function hasAcceptedTOS(): Promise<boolean>;
    function hasAcceptedUsagePermission(): Promise<boolean>;
    function getUserId(): Promise<string>;
    function sendEvent(event: number, extra?: string, params?: PlaytimeParams, uaChannel?: string): Promise<void>;
}
export default _default;
//# sourceMappingURL=Playtime.d.ts.map