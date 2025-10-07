/**
 * Holds information about the rewards.
 * @deprecated
 */
export interface PlaytimeReward {
    /**
     * Returns the overall amount of rewards that the user has collected so far (paid out +
     * available for payout).
    */
    reward: number;
    
    /**
     * Returns the amount of rewards which are available for payout.
    */
    alreadySpent: number;

    /**
     * Returns the amount of rewards that the user has already paid out.
    */
    availableForPayout: number;
}

