// RNPlaytimeStudio.m

#import "RNPlaytimeStudio.h"

@implementation RNPlaytimeStudio

RCT_EXPORT_MODULE(PlaytimeStudio)

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

- (NSDictionary *)constantsToExport
{
  return @{
    @"VERSION": @"4.0.0-beta.3",
  };
}

RCT_EXPORT_METHOD(
    getCampaigns:(NSDictionary *)paramsDictionary
    resolve:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeOptions *playtimeOptions = [[PlaytimeOptions alloc] initWithJSONObject:paramsDictionary error:&error];
    
    if (playtimeOptions == nil || error != nil) {
        reject(@"playtime_error", @"Invalid parameters for getCampaigns", error);
        return;
    }
    
    id tokensValue = paramsDictionary[@"tokens"];
    NSMutableArray<NSString *> *tokens;

    if ([tokensValue isKindOfClass:[NSArray class]]) {
        NSArray *rawArray = (NSArray *)tokensValue;
        NSMutableArray<NSString *> *stringArray = [NSMutableArray array];

        for (id item in rawArray) {
            if ([item isKindOfClass:[NSString class]]) {
                [stringArray addObject:(NSString *)item];
            }
        }

        if (stringArray.count > 0) {
            tokens = stringArray;
        }
    }
    
    if (tokens == nil) {
        [PlaytimeStudio getCampaignsWithOptions:playtimeOptions
                                       completionHandler: ^(PlaytimeCampaignsResponse * _Nullable response, NSError * _Nullable error) {
            if (error != nil) {
                RCTLogError(@"Error getting campaigns: %@", error);
                reject(@"playtime_error", @"Error getting campaigns", error);
                return;
            }
            
            NSDictionary *responseDictionary = [response toJSONObject];
            if (response == nil) {
                RCTLogError(@"Error parsing campaigns response");
                reject(@"playtime_error", @"Error parsing campaigns response", nil);
                return;
            }
            
            resolve(responseDictionary);
        }];
    } else {
        [PlaytimeStudio getCampaignsWithTokens:tokens
                                       options:playtimeOptions
                             completionHandler: ^(PlaytimeCampaignsResponse * _Nullable response, NSError * _Nullable error) {
            if (error != nil) {
                RCTLogError(@"Error getting campaigns with tokens: %@", error);
                reject(@"playtime_error", @"Error getting campaigns with tokens", error);
                return;
            }
            
            NSDictionary *responseDictionary = [response toJSONObject];
            if (response == nil) {
                RCTLogError(@"Error parsing campaigns with tokens response");
                reject(@"playtime_error", @"Error parsing campaigns with tokens response", nil);
                return;
            }
            
            resolve(responseDictionary);
        }];
    }
}

RCT_EXPORT_METHOD(
      getInstalledCampaigns:(NSDictionary *)paramsDictionary
      resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeOptions *playtimeOptions = [[PlaytimeOptions alloc] initWithJSONObject:paramsDictionary error:&error];
    
    if (playtimeOptions == nil || error != nil) {
        reject(@"playtime_error", @"Invalid parameters for getCampaigns", error);
        return;
    }
    
    [PlaytimeStudio getInstalledCampaignsWithOptions:playtimeOptions
                                   completionHandler: ^(PlaytimeCampaignsResponse * _Nullable response, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting installed apps: %@", error);
            reject(@"playtime_error", @"Error getting installed apps", error);
            return;
        }
        
        NSDictionary *responseDictionary = [response toJSONObject];
        if (response == nil) {
            RCTLogError(@"Error parsing installed apps response");
            reject(@"playtime_error", @"Error parsing installed apps response", nil);
            return;
        }
        
        resolve(responseDictionary);
    }];
}

RCT_EXPORT_METHOD(
    openInStore:(NSDictionary *)campaignDictionary
    resolver:(RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeCampaign *campaign = [[PlaytimeCampaign alloc] initWithJSONObject:campaignDictionary error:&error];
    
    if (campaign == nil || error != nil) {
        reject(@"playtime_error", @"Invalid parameters for openStore", error);
        return;
    }
    
    [PlaytimeStudio openInStoreWithCampaign:campaign
                  completionHandler:^(NSError * _Nullable error) {
        if (!error) {
            RCTLog(@"Opened store successfully");
            resolve(nil);
        } else {
            RCTLogError(@"Error opening store: %@", error);
            reject(@"playtime_error", @"Open store error", error);
        }
    }];
}

RCT_EXPORT_METHOD(
    openInstalledCampaign:(NSDictionary *)campaignDictionary
    resolver:(RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject)
{
    NSError *error = nil;
    PlaytimeCampaign *campaign = [[PlaytimeCampaign alloc] initWithJSONObject:campaignDictionary error:&error];
    
    if (campaign == nil || error != nil) {
        reject(@"playtime_error", @"Invalid parameters for openStore", error);
        return;
    }
    
    [PlaytimeStudio openInstalledCampaignWithCampaign:campaign
                  completionHandler:^(NSError * _Nullable error) {
        if (!error) {
            RCTLog(@"Opened store successfully");
            resolve(nil);
        } else {
            RCTLogError(@"Error opening store: %@", error);
            reject(@"playtime_error", @"Open store error", error);
        }
    }];
}

RCT_EXPORT_METHOD(
    getPermissions:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [PlaytimeStudio getPermissionsWithCompletionHandler:^(PlaytimePermissionsResponse * _Nullable response, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting permissions: %@", error);
            reject(@"playtime_error", @"Error getting permissions", error);
            return;
        }
        
        NSDictionary *responseDictionary = [response toJSONObject];
        if (response == nil) {
            RCTLogError(@"Error parsing permissions response");
            reject(@"playtime_error", @"Error parsing permissions response", nil);
            return;
        }
        
        resolve(responseDictionary);
    }];
}

RCT_EXPORT_METHOD(
    showPermissionsPrompt:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject)
{
    [PlaytimeStudio showPermissionsPromptWithCompletionHandler:^(PlaytimePermissionsResponse * _Nullable response, NSError * _Nullable error) {
        if (error != nil) {
            RCTLogError(@"Error getting permissions: %@", error);
            reject(@"playtime_error", @"Error getting permissions", error);
            return;
        }
        
        NSDictionary *responseDictionary = [response toJSONObject];
        if (response == nil) {
            RCTLogError(@"Error parsing permissions response");
            reject(@"playtime_error", @"Error parsing permissions response", nil);
            return;
        }
        
        resolve(responseDictionary);
    }];
}

@end
