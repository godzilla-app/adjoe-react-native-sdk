#import "RNPlaytimeBasic.h"

@implementation RNPlaytimeBasic

// Use the same name as on Android for compatibility
RCT_EXPORT_MODULE(RNPlaytimeSdk)

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

RCT_EXPORT_METHOD(
    showCatalogWithOptions:(NSDictionary *)paramsDictionary
    resolve:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeOptions *playtimeOptions = [[PlaytimeOptions alloc] initWithJSONObject:paramsDictionary error:&error];
    
    if (playtimeOptions == nil || error != nil) {
        reject(@"playtime_error", @"Invalid parameters for showCatalog", error);
        return;
    }
    
    [Playtime showCatalogWithOptions:playtimeOptions
                   completionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error showing Playtime catalog: %@", error);
            reject(@"playtime_error", @"Playtime catalog error", error);
            return;
        }
        
        resolve(nil);
    }];
}

@end
