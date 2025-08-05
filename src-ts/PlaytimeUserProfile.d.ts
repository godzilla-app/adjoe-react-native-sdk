export type PlaytimeGender = 'male' | 'female' | 'unknown';
export interface PlaytimeUserProfile {
    gender?: PlaytimeGender | null;
    birthday?: string | null;
}