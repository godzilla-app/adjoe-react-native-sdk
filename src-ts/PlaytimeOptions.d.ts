import { PlaytimeExtension } from "./PlaytimeExtension";
import { PlaytimeParams } from "./PlaytimeParams";
import { PlaytimeUserProfile } from "./PlaytimeUserProfile";

export interface PlaytimeOptions {
    userId?: string | null;
    userProfile?: PlaytimeUserProfile | null;
    params?: PlaytimeParams | null;
    extensions?: PlaytimeExtension | null;
    tokens?: string[] | null;
}