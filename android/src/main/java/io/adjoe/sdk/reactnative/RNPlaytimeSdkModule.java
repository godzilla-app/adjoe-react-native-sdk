package io.adjoe.sdk.reactnative;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.adjoe.sdk.Playtime;
import io.adjoe.sdk.PlaytimeException;
import io.adjoe.sdk.PlaytimeExtensions;
import io.adjoe.sdk.PlaytimeGender;
import io.adjoe.sdk.PlaytimeInitialisationListener;
import io.adjoe.sdk.PlaytimeNotInitializedException;
import io.adjoe.sdk.PlaytimeOptions;
import io.adjoe.sdk.PlaytimeParams;
import io.adjoe.sdk.PlaytimeUserProfile;
import io.adjoe.sdk.custom.PlaytimeAdvancePlusConfig;
import io.adjoe.sdk.custom.PlaytimeAdvancePlusEvent;
import io.adjoe.sdk.custom.PlaytimeCampaignListener;
import io.adjoe.sdk.custom.PlaytimeCampaignResponse;
import io.adjoe.sdk.custom.PlaytimeCampaignResponseError;
import io.adjoe.sdk.custom.PlaytimeCoinSetting;
import io.adjoe.sdk.custom.PlaytimeCustom;
import io.adjoe.sdk.custom.PlaytimePayoutError;
import io.adjoe.sdk.custom.PlaytimePayoutListener;
import io.adjoe.sdk.custom.PlaytimeRewardListener;
import io.adjoe.sdk.custom.PlaytimeRewardResponse;
import io.adjoe.sdk.custom.PlaytimeRewardResponseError;
import io.adjoe.sdk.custom.PlaytimeStreakInfo;
import io.adjoe.sdk.custom.AppDetails;
import io.adjoe.sdk.custom.CategoryTranslation;
import io.adjoe.sdk.internal.PlaytimePartnerApp;
import io.adjoe.sdk.internal.PlaytimePromoEvent;

@SuppressWarnings("unused")
public class RNPlaytimeSdkModule extends ReactContextBaseJavaModule {

    private static final DateFormat ISO_8601 = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.US);
    private static final DateFormat BIRTHDAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    static final Map<String, PlaytimePartnerApp> PARTNER_APPS = new HashMap<>();
    static WebViewSupplier webViewSupplier;
    private static PhoneVerificationSupplier phoneVerificationSupplier;
    private final ReactApplicationContext reactContext;

    public RNPlaytimeSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNPlaytimeSdk";
    }

    /* -----------------------------
             CONSTANTS START
       ----------------------------- */

    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put("PAYOUT_NOT_ENOUGH_COINS", PlaytimePayoutError.NOT_ENOUGH_COINS);
        constants.put("PAYOUT_TOS_NOT_ACCEPTED", PlaytimePayoutError.TOS_NOT_ACCEPTED);
        constants.put("PAYOUT_UNKNOWN", PlaytimePayoutError.UNKNOWN);
        constants.put("VERSION", Playtime.getVersion());
        constants.put("VERSION_NAME", Playtime.getVersionName());
        constants.put("EVENT_AGB_SHOWN", PlaytimeCustom.EVENT_AGB_SHOWN);
        constants.put("EVENT_AGB_ACCEPTED", PlaytimeCustom.EVENT_AGB_ACCEPTED);
        constants.put("EVENT_AGB_DECLINED", PlaytimeCustom.EVENT_AGB_DECLINED);
        constants.put("EVENT_USAGE_PERMISSION_ACCEPTED", PlaytimeCustom.EVENT_USAGE_PERMISSION_ACCEPTED);
        constants.put("EVENT_USAGE_PERMISSION_DENIED", PlaytimeCustom.EVENT_USAGE_PERMISSION_DENIED);
        constants.put("EVENT_VIDEO_PLAY", PlaytimeCustom.EVENT_VIDEO_PLAY);
        constants.put("EVENT_VIDEO_PAUSE", PlaytimeCustom.EVENT_VIDEO_PAUSE);
        constants.put("EVENT_VIDEO_ENDED", PlaytimeCustom.EVENT_VIDEO_ENDED);
        constants.put("EVENT_CAMPAIGNS_SHOWN", PlaytimeCustom.EVENT_CAMPAIGNS_SHOWN);
        constants.put("EVENT_APP_OPEN", PlaytimeCustom.EVENT_APP_OPEN);
        constants.put("EVENT_FIRST_IMPRESSION", PlaytimeCustom.EVENT_FIRST_IMPRESSION);
        constants.put("EVENT_TEASER_SHOWN", Playtime.EVENT_TEASER_SHOWN);
        return constants;
    }

    /* -----------------------------
              CONSTANTS END
       ----------------------------- */

    /* -----------------------------
           STATIC METHODS START
    ----------------------------- */

    public static void setWebViewSupplier(WebViewSupplier supplier) {
        RNPlaytimeSdkModule.webViewSupplier = supplier;
    }

    public static void setPhoneVerificationSupplier(PhoneVerificationSupplier supplier) {
        RNPlaytimeSdkModule.phoneVerificationSupplier = supplier;
    }

    /* -----------------------------
            STATIC METHODS END
    ----------------------------- */

    /* -----------------------------
      BASIC INTEGRATION METHODS START
       ----------------------------- */

    @ReactMethod
    public void init(String apiKey, ReadableMap optionsMap, final Promise promise) {
        try {
            PlaytimeOptions options = new PlaytimeOptions();
            if (optionsMap != null) {
                if (optionsMap.hasKey("userId")) {
                    options.setUserId(optionsMap.getString("userId"));
                }
                if (optionsMap.hasKey("applicationProcessName")) {
                    options.setApplicationProcessName(optionsMap.getString("applicationProcessName"));
                }
                // get playtime params from options
                if (optionsMap.hasKey("playtimeParams")) {
                    ReadableMap paramsMap = optionsMap.getMap("playtimeParams");
                    PlaytimeParams params = constructPlaytimeParams(paramsMap);
                    options.setParams(params);
                }
                if (optionsMap.hasKey("playtimeExtension")) {
                    ReadableMap extensionMap = optionsMap.getMap("playtimeExtension");
                    PlaytimeExtensions extensions = constructPlaytimeExtension(extensionMap);
                    options.setExtensions(extensions);
                }
                if (optionsMap.hasKey("playtimeUserProfile")) {
                    ReadableMap userProfileMap = optionsMap.getMap("playtimeUserProfile");
                    PlaytimeUserProfile userProfile = constructPlaytimeUserProfile(userProfileMap);
                    options.setUserProfile(userProfile);
                }
            }
            options.w("RN");

            Playtime.init(reactContext, apiKey, options, new PlaytimeInitialisationListener() {

                @Override
                public void onInitialisationFinished() {
                    promise.resolve(null);
                }

                @Override
                public void onInitialisationError(Exception e) {
                    promise.reject(e);
                }
            });
        } catch (Exception e) {
            Log.w("RNAdjoeSDK", e);
            promise.reject(e);
        }
    }

    @ReactMethod
    public void showCatalog(ReadableMap configMap, Promise promise) {
        Intent catalogIntent;
        try {
            PlaytimeParams params = constructPlaytimeParams(configMap);
            catalogIntent = Playtime.getCatalogIntent(reactContext, params);
        } catch (PlaytimeException e) {
            promise.reject(e);
            return;
        }
        try {
            if (getCurrentActivity() != null) {
                getCurrentActivity().startActivity(catalogIntent);
                promise.resolve(null);
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    catalogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                reactContext.startActivity(catalogIntent);
                promise.resolve(null);
            }
        } catch (Exception e) {
            Log.w("RNAdjoeSDK", e);
            promise.reject(e);
        }
    }

    @ReactMethod
    public void requestRewards(ReadableMap paramsMap, final Promise promise) {
        PlaytimeParams params = constructPlaytimeParams(paramsMap);
        PlaytimeCustom.requestRewards(reactContext, params, new PlaytimeRewardListener() {

            @Override
            public void onUserReceivesReward(PlaytimeRewardResponse playtimeRewardResponse) {
                if (playtimeRewardResponse != null) {
                    WritableMap map = Arguments.createMap();
                    map.putInt("reward", playtimeRewardResponse.reward);
                    map.putInt("alreadySpent", playtimeRewardResponse.alreadySpentCoins);
                    map.putInt("availableForPayout",
                            playtimeRewardResponse.availablePayoutCoins);
                    promise.resolve(map);
                } else { // should never happen
                    promise.resolve(null);
                }
            }

            @Override
            public void onUserReceivesRewardError(
                    PlaytimeRewardResponseError playtimeRewardResponseError) {
                if (playtimeRewardResponseError != null
                        && playtimeRewardResponseError.exception != null) {
                    promise.reject(playtimeRewardResponseError.exception);
                } else { // should never happen
                    promise.reject("", "");
                }
            }
        });
    }

    @ReactMethod
    public void doPayout(ReadableMap paramsMap, final Promise promise) {
        PlaytimeParams params = constructPlaytimeParams(paramsMap);
        PlaytimeCustom.doPayout(reactContext, params, new PlaytimePayoutListener() {

            @Override
            public void onPayoutExecuted(int i) {
                promise.resolve(i);
            }

            @Override
            public void onPayoutError(PlaytimePayoutError playtimePayoutError) {
                if (playtimePayoutError != null) {
                    promise.reject(String.valueOf(playtimePayoutError.getReason()), "",
                            playtimePayoutError.getException());
                } else { // should never happen
                    promise.reject("", "");
                }
            }
        });
    }

    @ReactMethod
    public void setUAParams(ReadableMap paramsMap, final Promise promise) {
        PlaytimeParams params = constructPlaytimeParams(paramsMap);
        Playtime.setUAParams(reactContext, params);
        promise.resolve(null);
    }


    /* -----------------------------
       BASIC INTEGRATION METHODS END
       ----------------------------- */

    /* -----------------------------
     ADVANCED INTEGRATION METHODS START
       ----------------------------- */

    @ReactMethod
    public void requestPartnerApps(ReadableMap paramsMap, final Promise promise) {
        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        PlaytimeParams params = constructPlaytimeParams(paramsMap);
        PlaytimeCustom.requestPartnerApps(reactContext, webViewContainer, params,
                new PlaytimeCampaignListener() {

                    @Override
                    public void onCampaignsReceived(
                            PlaytimeCampaignResponse playtimeCampaignResponse) {
                        WritableArray apps = Arguments.createArray();
                        for (PlaytimePartnerApp app : playtimeCampaignResponse.partnerApps) {
                            apps.pushMap(partnerAppToWritableMap(app));

                            PARTNER_APPS.put(app.getPackageName(), app);
                        }
                        promise.resolve(apps);
                    }

                    @Override
                    public void onCampaignsReceivedError(
                            PlaytimeCampaignResponseError playtimeCampaignResponseError) {
                        if (playtimeCampaignResponseError.exception != null) {
                            promise.reject(playtimeCampaignResponseError.exception);
                        } else {
                            promise.reject("", "");
                        }
                    }
                });
    }

    @ReactMethod
    public void executePartnerAppClick(final String packageName, ReadableMap map,
            final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        PlaytimeParams params = constructPlaytimeParams(map);
        partnerApp.executeClick(
            reactContext, 
            webViewContainer, 
            params, 
            new PlaytimePartnerApp.ClickListener() {

                    @Override
                    public void onFinished() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onError() {
                        promise.reject(
                                new RuntimeException("Could not execute click for " + packageName));
                    }

                    @Override
                    public void onAlreadyClicking() {
                        promise.resolve("already_clicking");
                    }
                });
    }

    @ReactMethod
    public void executeShowCampaignDetailClick(final String packageName, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        partnerApp.executeShowCampaignDetailClick(
            reactContext, 
            new PlaytimePartnerApp.ClickListener() {

                @Override
                public void onFinished() {
                    promise.resolve(null);
                }

                @Override
                public void onError() {
                    promise.reject(
                            new RuntimeException("Could not execute detail click for " + packageName));
                }

                @Override
                public void onAlreadyClicking() {
                    promise.resolve("already_clicking");
                }
                });
    }

    @ReactMethod
    public void launchPartnerApp(final String packageName, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }
        if (packageName.isEmpty()) {
            return;
        }

        try {
            final Intent launchIntent = reactContext.getPackageManager().getLaunchIntentForPackage(
                    packageName);
            if (launchIntent != null) {
                reactContext.startActivity(launchIntent);
                promise.resolve(true);
                return;
            }
        } catch (Exception ignored) {
        }

        try {
            final Intent marketLaunchIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName));
            marketLaunchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
            marketLaunchIntent.setPackage("com.android.vending");
            reactContext.startActivity(marketLaunchIntent);
            promise.resolve(true);
        } catch (Exception exception) {
            promise.reject(exception);
        }
    }

    @ReactMethod
    public void executePartnerAppView(final String packageName, ReadableMap map,
            final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        PlaytimeParams params = constructPlaytimeParams(map);
        partnerApp.executeView(reactContext, webViewContainer, params,
                new PlaytimePartnerApp.ViewListener() {

                    @Override
                    public void onFinished() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onError() {
                        promise.reject(
                                new RuntimeException("Could not execute view for " + packageName));
                    }

                    @Override
                    public void onAlreadyViewing() {
                        promise.resolve("already_viewing");
                    }
                });
    }

    @ReactMethod
    public void getRemainingTimeForPartnerApp(String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        long remainingTime = partnerApp.getRemainingUntilNextReward(reactContext);
        promise.resolve(String.valueOf(remainingTime));
    }

    @ReactMethod
    public void getNextRewardLevelForPartnerApp(String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name"));
            return;
        }

        PlaytimePartnerApp.RewardLevel level = partnerApp.getNextRewardLevel(reactContext);
        if (level == null) {
            promise.reject(new NullPointerException("partner app has no next reward level"));
        } else {
            promise.resolve(rewardLevelToWritableMap(level));
        }
    }

    @ReactMethod
    public void requestInstalledPartnerApps(ReadableMap map, final Promise promise) {
        PlaytimeParams params = constructPlaytimeParams(map);
        PlaytimeCustom.requestInstalledPartnerApps(reactContext, params,
                new PlaytimeCampaignListener() {

                    @Override
                    public void onCampaignsReceived(PlaytimeCampaignResponse playtimeCampaignResponse) {
                        WritableArray apps = Arguments.createArray();
                        for (PlaytimePartnerApp app : playtimeCampaignResponse.partnerApps) {
                            apps.pushMap(partnerAppToWritableMap(app));

                            PARTNER_APPS.put(app.getPackageName(), app);
                        }
                        promise.resolve(apps);
                    }

                    @Override
                    public void onCampaignsReceivedError(
                            PlaytimeCampaignResponseError playtimeCampaignResponseError) {
                        if (playtimeCampaignResponseError.exception != null) {
                            promise.reject(playtimeCampaignResponseError.exception);
                        } else {
                            promise.reject("", "");
                        }
                    }
                });
    }

    @ReactMethod
    public void getInstallDate(String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        PlaytimePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException(
                    "no partner app found for package name " + packageName));
            return;
        }

        Date installDate = partnerApp.getInstallDate();
        if (installDate == null) {
            promise.reject(
                    new NullPointerException("no time found for this partner app" + packageName));
            return;
        }

        promise.resolve(ISO_8601.format(installDate));
    }

    @ReactMethod
    public void _a(boolean a, final Promise promise) {
        try {
            Playtime.a(a);
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setTosAccepted(final Promise promise) {
        PlaytimeCustom.setTosAccepted(reactContext, new PlaytimeInitialisationListener() {

            @Override
            public void onInitialisationFinished() {
                promise.resolve(null);
            }

            @Override
            public void onInitialisationError(Exception e) {
                promise.reject(e);
            }
        });
    }

    @ReactMethod
    public void setUsagePermissionAccepted(final Promise promise) {
        PlaytimeCustom.setUsagePermissionAccepted(reactContext,
                new PlaytimeInitialisationListener() {

                    @Override
                    public void onInitialisationFinished() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onInitialisationError(Exception e) {
                        promise.reject(e);
                    }
                });
    }

    @ReactMethod
    public void showUsagePermissionScreen(final Promise promise) {
        try {
            Activity localActivity = reactContext.getCurrentActivity();
            if (localActivity != null) {
                PlaytimeCustom.showUsagePermissionScreen(reactContext.getCurrentActivity());
                promise.resolve(null);
            } else {
                promise.reject(new PlaytimeNotInitializedException("Not initialized"));
            }
        } catch (PlaytimeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void sendEvent(int event, String extra, ReadableMap map) {
        try {
            PlaytimeParams params = constructPlaytimeParams(map);
            Playtime.sendUserEvent(reactContext, event, extra, params);
        } catch (PlaytimeNotInitializedException e) {
            Log.w("RNAdjoeSDK", e);
        }
    }

    /* -----------------------------
      ADVANCED INTEGRATION METHODS END
       ----------------------------- */


    /* -----------------------------
          UTILITY METHODS START
       ----------------------------- */

    @ReactMethod
    public void getVersion(Promise promise) {
        promise.resolve(Playtime.getVersion());
    }

    @ReactMethod
    public void getVersionName(Promise promise) {
        promise.resolve(Playtime.getVersionName());
    }

    @ReactMethod
    public void isInitialized(Promise promise) {
        promise.resolve(Playtime.isInitialized());
    }

    @ReactMethod
    public void hasAcceptedTOS(Promise promise) {
        promise.resolve(Playtime.hasAcceptedTOS(reactContext));
    }

    @ReactMethod
    public void hasAcceptedUsagePermission(Promise promise) {
        promise.resolve(Playtime.hasAcceptedUsagePermission(reactContext));
    }

    @ReactMethod
    public void getUserId(Promise promise) {
        promise.resolve(Playtime.getUserId(reactContext));
    }

    /* -----------------------------
           UTILITY METHODS END
       ----------------------------- */

    /* -----------------------------
           FRAUD METHODS START
    ----------------------------- */

    @ReactMethod
    public void faceVerification(final Promise promise) {
        if (phoneVerificationSupplier == null) {
            promise.reject("0",
                    "You must call RNAdjoeSdkModule.setPhoneVerificationSupplier first");
            return;
        }

        Activity activity = phoneVerificationSupplier.getActivity();

        if (activity == null) {
            promise.reject("0", "phoneVerificationSupplier.getActivity() == null");
            return;
        }

        PlaytimeCustom.faceVerification(activity, new PlaytimeCustom.FaceVerificationCallback() {

            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onAlreadyVerified() {
                promise.reject("1", "already_verified");
            }

            @Override
            public void onCancel() {
                promise.reject("2", "cancel");
            }

            @Override
            public void onNotInitialized() {
                promise.reject("3", "not_initialized");
            }

            @Override
            public void onTosIsNotAccepted() {
                promise.reject("4", "tos_not_accepted");
            }

            @Override
            public void onLivenessCheckFailed() {
                promise.reject("5", "liveness_check_failed");
            }

            @Override
            public void onError(Exception exception) {
                promise.reject("0", exception.getMessage(), exception);
            }

            @Override
            public void onPendingReview() {
                promise.reject("7", "pending_review");
            }

            @Override
            public void onMaxAttemptsReached() {
                promise.reject("8", "max_attempts_reached");
            }
        });
    }

    @ReactMethod
    public void faceVerificationStatus(final Promise promise) {
        try {
            PlaytimeCustom.faceVerificationStatus(reactContext,
                    new PlaytimeCustom.FaceVerificationStatusCallback() {

                        @Override
                        public void onVerified() {
                            promise.resolve(null);
                        }

                        @Override
                        public void onNotVerified() {
                            promise.reject("1", "not_verified");
                        }

                        @Override
                        public void onNotInitialized() {
                            promise.reject("2", "not_initialized");
                        }

                        @Override
                        public void onTosIsNotAccepted() {
                            promise.reject("3", "tos_not_accepted");
                        }

                        @Override
                        public void onError(Exception exception) {
                            promise.reject("0", exception.getMessage(), exception);
                        }

                        @Override
                        public void onPendingReview() {
                            promise.reject("4", "pending_review");
                        }

                        @Override
                        public void onMaxAttemptsReached() {
                            promise.reject("5", "max_attempts_reached");
                        }
                    });
        } catch (PlaytimeNotInitializedException e) {
            promise.reject("0", e.getMessage(), e);
        }
    }

    /* -----------------------------
            FRAUD METHODS END
    ----------------------------- */

    // region helper methods
    private PlaytimeParams constructPlaytimeParams(ReadableMap paramsMap) {
        PlaytimeParams.Builder builder = new PlaytimeParams.Builder();
        if (paramsMap != null) {
            if (paramsMap.hasKey("placement")) {
                builder.setPlacement(paramsMap.getString("placement"));
            }
            if (paramsMap.hasKey("uaNetwork")) {
                builder.setUaNetwork(paramsMap.getString("uaNetwork"));
            }
            if (paramsMap.hasKey("uaChannel")) {
                builder.setUaChannel(paramsMap.getString("uaChannel"));
            }
            if (paramsMap.hasKey("uaSubPublisherCleartext")) {
                builder.setUaSubPublisherCleartext(paramsMap.getString("uaSubPublisherCleartext"));
            }
            if (paramsMap.hasKey("uaSubPublisherEncrypted")) {
                builder.setUaSubPublisherEncrypted(paramsMap.getString("uaSubPublisherEncrypted"));
            }
        }
        return builder.build();
    }

    private PlaytimeExtensions constructPlaytimeExtension(ReadableMap extensionMap) {
        PlaytimeExtensions.Builder extensions = new PlaytimeExtensions.Builder();
        if (extensionMap == null) return extensions.build();
        return extensions.setSubId1(extensionMap.getString("subId1"))
                .setSubId2(extensionMap.getString("subId2"))
                .setSubId3(extensionMap.getString("subId3"))
                .setSubId4(extensionMap.getString("subId4"))
                .setSubId5(extensionMap.getString("subId5"))
                .build();
    }

    private PlaytimeUserProfile constructPlaytimeUserProfile(ReadableMap userProfileMap) {
        if (userProfileMap == null) return null;
        String gender = userProfileMap.getString("gender");
        PlaytimeGender playtimeGender;
        if ("male".equalsIgnoreCase(gender)) {
            playtimeGender = PlaytimeGender.MALE;
        } else if ("female".equalsIgnoreCase(gender)) {
            playtimeGender = PlaytimeGender.FEMALE;
        } else {
            playtimeGender = PlaytimeGender.UNKNOWN;
        }

        String birthdate = userProfileMap.getString("birthday");
        Date birthday = null;
        if (!TextUtils.isEmpty(birthdate)) {
            try {
                birthday = DateFormat.getDateInstance().parse(birthdate);
            } catch (ParseException ignore) {
            }
        }
        return new PlaytimeUserProfile(playtimeGender, birthday);
    }


    // endregion helper method

    private WritableMap partnerAppToWritableMap(PlaytimePartnerApp app) {
        WritableMap appMap = Arguments.createMap();
        appMap.putString("campaignType", app.getCampaignType());
        appMap.putString("name", app.getName());
        appMap.putString("packageName", app.getPackageName());
        appMap.putString("description", app.getDescription());
        appMap.putString("iconUrl", app.getIconURL());
        appMap.putString("landscapeImageUrl", app.getLandscapeImageURL());
        appMap.putString("videoUrl", app.getVideoURL());
        appMap.putBoolean("inAppPurchaseEnabled", app.isInAppPurchaseEnabled());
        appMap.putDouble("createdAt",
                app.getCreatedAt() == null ? -1 : app.getCreatedAt().getTime());
        appMap.putDouble("multiplier", app.getMultiplier());
        appMap.putString("category", app.getAppCategory());
        appMap.putString("portraitImageUrl", app.getPortraitImageURL());
        appMap.putString("portraitVideoUrl", app.getPortraitVideoURL());
        AppDetails appDetails = app.getAppDetails();
        if (appDetails != null) {
            appMap.putMap("appDetails", appDetailsToWritableMap(appDetails));
        }

        if (app.getAdvanceRewardCoins() > 0) {
            appMap.putInt("postInstallRewardEventCoins", app.getAdvanceRewardCoins());
            appMap.putInt("advanceRewardCoins", app.getAdvanceRewardCoins());
        }

        PlaytimePromoEvent event = app.getEvent();
        if (event != null && event.isRunningNow()) {
            appMap.putString("promoStartDate", String.valueOf(event.getStartDate().getTime()));
            appMap.putString("promoEndDate", String.valueOf(event.getEndDate().getTime()));
            appMap.putDouble("promoFactor", event.getFactor());
        }

        WritableArray rewardConfig = Arguments.createArray();
        for (PlaytimePartnerApp.RewardLevel level : app.getRewardConfig()) {
            rewardConfig.pushMap(rewardLevelToWritableMap(level));
        }

        appMap.putArray("rewardConfig", rewardConfig);
        // new
        appMap.putInt("advanceDailyLimit", app.getAdvanceDailyLimit());
        appMap.putInt("advanceTotalLimit", app.getAdvanceTotalLimit());
        appMap.putInt("advancePlusCoins", app.getAdvancePlusCoins());
        appMap.putString("advancePlusActionDescription", app.getAdvancePlusActionDescription());
        appMap.putString("advancePlusRewardAction", app.getAdvancePlusRewardedAction());

        // streak info for request app
        appMap.putBoolean("isInCoinStreakExperiment", app.isInCoinStreakExperiment());
        appMap.putInt("coinStreakMaxCoinAmount", app.getCoinStreakMaxCoinAmount());

        // streak info for rewarded
        PlaytimeStreakInfo streakInfo = app.getStreakInfo();
        if (streakInfo != null) {
            WritableMap streakInfoMap = Arguments.createMap();
            streakInfoMap.putBoolean("streakInfoFailed", streakInfo.isFailed());
            streakInfoMap.putInt("streakInfoLastAchievedDay", streakInfo.getLastAchievedDay());
            WritableArray coinSettingsArray = Arguments.createArray();
            List<PlaytimeCoinSetting> coinSettings = streakInfo.getCoinSettings();
            for (PlaytimeCoinSetting coinSetting : coinSettings) {
                WritableMap coinSettingMap = Arguments.createMap();
                coinSettingMap.putInt("day", coinSetting.getDay());
                coinSettingMap.putInt("coins", coinSetting.getCoins());
                coinSettingsArray.pushMap(coinSettingMap);
            }
            streakInfoMap.putArray("coinSettings", coinSettingsArray);
            appMap.putMap("streakInfo", streakInfoMap);
        }

        PlaytimeAdvancePlusConfig advancePlusConfig = app.getAdvancePlusConfig();
        if (advancePlusConfig != null) {
            WritableMap advancePlusConfigMap = Arguments.createMap();
            advancePlusConfigMap.putInt("totalCoins", advancePlusConfig.getTotalCoins());
            advancePlusConfigMap.putInt("highestBonusEventCoins",
                    advancePlusConfig.getHighestBonusEventCoins());
            advancePlusConfigMap.putInt("highestSequentialEventCoins",
                    advancePlusConfig.getHighestSequentialEventCoins());

            WritableArray sequentialEventsArray = createEventsWritableArray(advancePlusConfig.getSequentialEvents());
            advancePlusConfigMap.putArray("sequentialEvents", sequentialEventsArray);

            WritableArray bonusEventsArray = createEventsWritableArray(advancePlusConfig.getBonusEvents());
            advancePlusConfigMap.putArray("bonusEvents", bonusEventsArray);

            appMap.putMap("advancePlusConfig", advancePlusConfigMap);
        }
        return appMap;
    }

    private WritableArray createEventsWritableArray(List<PlaytimeAdvancePlusEvent> events) {
        WritableArray eventsArray = Arguments.createArray();

        for (PlaytimeAdvancePlusEvent event : events) {
            WritableMap eventMap = Arguments.createMap();
            eventMap.putString("name", event.getName());
            eventMap.putString("description", event.getDescription());
            eventMap.putInt("coins", event.getCoins());

            String rewardedAt = event.getRewardedAt();
            if (rewardedAt != null && !rewardedAt.isEmpty()) {
                eventMap.putString("rewardedAt", rewardedAt);
            }

            eventsArray.pushMap(eventMap);
        }
        return eventsArray;
    }

    static WritableMap rewardLevelToWritableMap(PlaytimePartnerApp.RewardLevel level) {
        WritableMap map = Arguments.createMap();
        if (level == null) {
            return map;
        }

        map.putInt("level", level.getLevel());
        map.putDouble("seconds", level.getSeconds());
        map.putDouble("value", level.getValue());
        return map;
    }

    static WritableMap appDetailsToWritableMap(AppDetails details) {
        WritableMap map = Arguments.createMap();
        if (details == null) {
            return map;
        }
        map.putString("platform", details.getPlatform());
        map.putInt("androidVersion", details.getAndroidVersion());
        map.putString("rating", details.getRating());
        map.putString("numOfRatings", details.getNumOfRatings());
        map.putString("size", details.getSize());
        map.putString("installs", details.getInstalls());
        map.putString("ageRating", details.getAgeRating());
        map.putString("category", details.getCategory());
        map.putBoolean("hasInAppPurchases", details.getHasInAppPurchases());
        WritableArray array = Arguments.createArray();
        for (CategoryTranslation categoryTranslation : details.getCategoryTranslations()) {
            WritableMap categoryMap = Arguments.createMap();
            categoryMap.putString("name", categoryTranslation.getName());
            categoryMap.putString("language", categoryTranslation.getLanguage());
            array.pushMap(categoryMap);
        }
        map.putArray("categoryTranslations", array);
        return map;
    }

    public interface WebViewSupplier {

        FrameLayout getLayoutForWebView();
    }

    public interface PhoneVerificationSupplier {

        FragmentActivity getFragmentActivity();

        Activity getActivity();

        GoogleApiClient getGoogleApiClient();
    }
}
