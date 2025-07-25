import { PlaytimeExtension } from "./PlaytimeExtension";
import { PlaytimeParams } from "./PlaytimeParams";
import { PlaytimeUserProfile } from "./PlaytimeUserProfile";

export interface PlaytimeOptions {
    userId?: string;
    playtimeParams?: PlaytimeParams;
    applicationProcessName?: string;
    playtimeExtension?: PlaytimeExtension;
    playtimeUserProfile?: PlaytimeUserProfile;
}