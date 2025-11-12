
interface PlaytimeStatusDetails {
    isFraud: boolean;
}

export interface PlaytimeStatus {
    isInitialized: boolean;
    details: PlaytimeStatusDetails;
}
