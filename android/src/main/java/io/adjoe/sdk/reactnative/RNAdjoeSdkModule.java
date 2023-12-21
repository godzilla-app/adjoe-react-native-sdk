package io.adjoe.sdk.reactnative;

import android.app.Activity;
import android.content.Context;
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
import java.util.Locale;
import java.util.Map;

import io.adjoe.sdk.Adjoe;
import io.adjoe.sdk.AdjoeCampaignListener;
import io.adjoe.sdk.AdjoeCampaignResponse;
import io.adjoe.sdk.AdjoeCampaignResponseError;
import io.adjoe.sdk.AdjoeException;
import io.adjoe.sdk.AdjoeExtensions;
import io.adjoe.sdk.AdjoeGender;
import io.adjoe.sdk.AdjoeInitialisationListener;
import io.adjoe.sdk.AdjoeNotInitializedException;
import io.adjoe.sdk.AdjoeParams;
import io.adjoe.sdk.AdjoePartnerApp;
import io.adjoe.sdk.AdjoePayoutError;
import io.adjoe.sdk.AdjoePayoutListener;
import io.adjoe.sdk.AdjoePhoneVerification;
import io.adjoe.sdk.AdjoePromoEvent;
import io.adjoe.sdk.AdjoeRewardListener;
import io.adjoe.sdk.AdjoeRewardResponse;
import io.adjoe.sdk.AdjoeRewardResponseError;
import io.adjoe.sdk.AdjoeUsageManagerCallback;
import io.adjoe.sdk.AdjoeUserProfile;
import io.adjoe.sdk.AppDetails;
import io.adjoe.sdk.CategoryTranslation;
import io.adjoe.sdk.AdjoeStreakInfo;
import io.adjoe.sdk.AdjoeCoinSetting;
import io.adjoe.sdk.AdjoeAdvancePlusConfig;
import io.adjoe.sdk.AdjoeAdvancePlusEvent;
import java.util.List;

@SuppressWarnings("unused")
public class RNAdjoeSdkModule extends ReactContextBaseJavaModule {

    private static final DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.US);
    private static final DateFormat BIRTHDAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    static final Map<String, AdjoePartnerApp> PARTNER_APPS = new HashMap<>();
    static WebViewSupplier webViewSupplier;
    private static AdjoePhoneVerification phoneVerification;
    private static PhoneVerificationSupplier phoneVerificationSupplier;
    private final ReactApplicationContext reactContext;

    public RNAdjoeSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAdjoeSdk";
    }

    /* -----------------------------
             CONSTANTS START
       ----------------------------- */

    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put("PAYOUT_ALL_COINS", Adjoe.ALL_COINS);
        constants.put("PAYOUT_NOT_ENOUGH_COINS", AdjoePayoutError.NOT_ENOUGH_COINS);
        constants.put("PAYOUT_TOS_NOT_ACCEPTED", AdjoePayoutError.TOS_NOT_ACCEPTED);
        constants.put("PAYOUT_UNKNOWN", AdjoePayoutError.UNKNOWN);
        constants.put("VERSION", Adjoe.getVersion());
        constants.put("VERSION_NAME", Adjoe.getVersionName());
        constants.put("EVENT_AGB_SHOWN", Adjoe.EVENT_AGB_SHOWN);
        constants.put("EVENT_AGB_ACCEPTED", Adjoe.EVENT_AGB_ACCEPTED);
        constants.put("EVENT_AGB_DECLINED", Adjoe.EVENT_AGB_DECLINED);
        constants.put("EVENT_USAGE_PERMISSION_ACCEPTED", Adjoe.EVENT_USAGE_PERMISSION_ACCEPTED);
        constants.put("EVENT_USAGE_PERMISSION_DENIED", Adjoe.EVENT_USAGE_PERMISSION_DENIED);
        constants.put("EVENT_INSTALL_CLICKED", Adjoe.EVENT_INSTALL_CLICKED);
        constants.put("EVENT_VIDEO_PLAY", Adjoe.EVENT_VIDEO_PLAY);
        constants.put("EVENT_VIDEO_PAUSE", Adjoe.EVENT_VIDEO_PAUSE);
        constants.put("EVENT_VIDEO_ENDED", Adjoe.EVENT_VIDEO_ENDED);
        constants.put("EVENT_CAMPAIGNS_SHOWN", Adjoe.EVENT_CAMPAIGNS_SHOWN);
        constants.put("EVENT_CAMPAIGN_VIEW", Adjoe.EVENT_CAMPAIGN_VIEW);
        constants.put("EVENT_APP_OPEN", Adjoe.EVENT_APP_OPEN);
        constants.put("EVENT_FIRST_IMPRESSION", Adjoe.EVENT_FIRST_IMPRESSION);
        constants.put("EVENT_TEASER_SHOWN", Adjoe.EVENT_TEASER_SHOWN);
        return constants;
    }

    /* -----------------------------
              CONSTANTS END
       ----------------------------- */

    /* -----------------------------
           STATIC METHODS START
    ----------------------------- */

    public static void setWebViewSupplier(WebViewSupplier supplier) {
        RNAdjoeSdkModule.webViewSupplier = supplier;
    }

    public static void setPhoneVerificationSupplier(PhoneVerificationSupplier supplier) {
        RNAdjoeSdkModule.phoneVerificationSupplier = supplier;
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (phoneVerification != null) {
            phoneVerification.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

    public static void onResume(Activity activity) {
        if (phoneVerification != null) {
            phoneVerification.onResume(activity);
        }
    }

    public static void onDestroy(Activity activity) {
        if (phoneVerification != null) {
            phoneVerification.onDestroy(activity);
        }
    }

    public static void setRecommendedAppsActivity(Context context, Class<? extends Activity> activity) throws RNAdjoeSdkException {
        try {
            Adjoe.setRecommendedAppsActivity(context, activity);
        } catch (AdjoeException e) {
            throw new RNAdjoeSdkException(e.getMessage(), e.getCause());
        }
    }

    public static void setInstalledAppsActivity(Context context, Class<? extends Activity> activity) throws RNAdjoeSdkException {
        try {
            Adjoe.setInstalledAppsActivity(context, activity);
        } catch (AdjoeException e) {
            throw new RNAdjoeSdkException(e.getMessage(), e.getCause());
        }
    }

    public static boolean isAdjoeProcess() {
        return Adjoe.isAdjoeProcess();
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
            Adjoe.Options options = new Adjoe.Options();
            if (optionsMap != null) {
                if (optionsMap.hasKey("user_id")) {
                    options.setUserId(optionsMap.getString("user_id"));
                }
                if (optionsMap.hasKey("applicationProcessName")) {
                    options.setApplicationProcessName(optionsMap.getString("applicationProcessName"));
                }
                // get adjoe params from options
                if (optionsMap.hasKey("adjoeParams")) {
                    ReadableMap paramsMap = optionsMap.getMap("adjoeParams");
                    AdjoeParams params = constructAdjoeParams(paramsMap);
                    options.setParams(params);
                }
                if (optionsMap.hasKey("adjoeExtension")) {
                    ReadableMap extensionMap = optionsMap.getMap("adjoeExtension");
                    AdjoeExtensions extensions = constructAdjoeExtension(extensionMap);
                    options.setExtensions(extensions);
                }
                if (optionsMap.hasKey("adjoeUserProfile")) {
                    ReadableMap userProfileMap = optionsMap.getMap("adjoeUserProfile");
                    AdjoeUserProfile userProfile = constructAdjoeUserProfile(userProfileMap);
                    options.setUserProfile(userProfile);
                }
            }
            options.w("RN");

            Adjoe.init(reactContext, apiKey, options, new AdjoeInitialisationListener() {

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
    public void showOfferwall(ReadableMap configMap, Promise promise) {
        Intent offerwallIntent;
        try {
            AdjoeParams params = constructAdjoeParams(configMap);
            offerwallIntent = Adjoe.getOfferwallIntent(reactContext, params);
        } catch (AdjoeException e) {
            promise.reject(e);
            return;
        }
        try {
            if (getCurrentActivity() != null) {
                getCurrentActivity().startActivity(offerwallIntent);
                promise.resolve(null);
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    offerwallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                reactContext.startActivity(offerwallIntent);
                promise.resolve(null);
            }
        } catch (Exception e) {
            Log.w("RNAdjoeSDK", e);
            promise.reject(e);
        }
    }

    @ReactMethod
    public void canShowOfferwall(Promise promise) {
        promise.resolve(Adjoe.canShowOfferwall(reactContext));
    }

    @ReactMethod
    public void canShowPostInstallRewardOfferwall(Promise promise) {
        promise.resolve(Adjoe.canShowPostInstallRewardOfferwall(reactContext));
    }

    @ReactMethod
    public void requestRewards(ReadableMap paramsMap, final Promise promise) {
        try {
            AdjoeParams params = constructAdjoeParams(paramsMap);
            Adjoe.requestRewards(reactContext, params, new AdjoeRewardListener() {

                @Override
                public void onUserReceivesReward(AdjoeRewardResponse adjoeRewardResponse) {
                    if (adjoeRewardResponse != null) {
                        WritableMap map = Arguments.createMap();
                        map.putInt("reward", adjoeRewardResponse.getReward());
                        map.putInt("already_spent", adjoeRewardResponse.getAlreadySpentCoins());
                        map.putInt("available_for_payout", adjoeRewardResponse.getAvailablePayoutCoins());
                        promise.resolve(map);
                    } else { // should never happen
                        promise.resolve(null);
                    }
                }

                @Override
                public void onUserReceivesRewardError(AdjoeRewardResponseError adjoeRewardResponseError) {
                    if (adjoeRewardResponseError != null && adjoeRewardResponseError.getException() != null) {
                        promise.reject(adjoeRewardResponseError.getException());
                    } else { // should never happen
                        promise.reject("", "");
                    }
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void doPayout(ReadableMap paramsMap, final Promise promise) {
        try {
            AdjoeParams params = constructAdjoeParams(paramsMap);
            Adjoe.doPayout(reactContext, params, new AdjoePayoutListener() {

                @Override
                public void onPayoutExecuted(int i) {
                    promise.resolve(i);
                }

                @Override
                public void onPayoutError(AdjoePayoutError adjoePayoutError) {
                    if (adjoePayoutError != null) {
                        promise.reject(String.valueOf(adjoePayoutError.getReason()), "", adjoePayoutError.getException());
                    } else { // should never happen
                        promise.reject("", "");
                    }
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setProfile(String source, String genderString, String birthdayString, ReadableMap paramsMap, Promise promise) {
        AdjoeGender gender = AdjoeGender.UNKNOWN;
        if ("male".equalsIgnoreCase(genderString)) {
            gender = AdjoeGender.MALE;
        } else if ("female".equalsIgnoreCase(genderString)) {
            gender = AdjoeGender.FEMALE;
        }

        Date birthday = null;
        try {
            birthday = BIRTHDAY_FORMAT.parse(birthdayString);
        } catch (ParseException e) {
            promise.reject(e);
            return;
        }

        try {
            AdjoeParams params = constructAdjoeParams(paramsMap);
            Adjoe.setProfile(reactContext, source, new AdjoeUserProfile(gender, birthday), params);
            promise.resolve(null);
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setUAParams(ReadableMap paramsMap, final Promise promise){
        AdjoeParams params = constructAdjoeParams(paramsMap);
        Adjoe.setUAParams(reactContext, params);
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
        try {
            FrameLayout webViewContainer = null;
            if (webViewSupplier != null) {
                webViewContainer = webViewSupplier.getLayoutForWebView();
            }
            AdjoeParams params = constructAdjoeParams(paramsMap);
            Adjoe.requestPartnerApps(reactContext, webViewContainer, params, new AdjoeCampaignListener() {

                @Override
                public void onCampaignsReceived(AdjoeCampaignResponse adjoeCampaignResponse) {
                    WritableArray apps = Arguments.createArray();
                    for (AdjoePartnerApp app : adjoeCampaignResponse.getPartnerApps()) {
                        apps.pushMap(partnerAppToWritableMap(app));

                        PARTNER_APPS.put(app.getPackageName(), app);
                    }
                    promise.resolve(apps);
                }

                @Override
                public void onCampaignsReceivedError(AdjoeCampaignResponseError adjoeCampaignResponseError) {
                    if (adjoeCampaignResponseError.getException() != null) {
                        promise.reject(adjoeCampaignResponseError.getException());
                    } else {
                        promise.reject("", "");
                    }
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void requestCampaignApps(ReadableMap map, final Promise promise) {
        try {
            FrameLayout webViewContainer = null;
            if (webViewSupplier != null) {
                webViewContainer = webViewSupplier.getLayoutForWebView();
            }
            AdjoeParams params = constructAdjoeParams(map);
            Adjoe.requestPartnerApps(reactContext, webViewContainer, params, new AdjoeCampaignListener() {

                @Override
                public void onCampaignsReceived(AdjoeCampaignResponse adjoeCampaignResponse) {
                    final WritableMap response = Arguments.createMap();
                    final WritableArray apps = Arguments.createArray();

                    if (adjoeCampaignResponse.hasPromoEvent()) {
                        AdjoePromoEvent promoEvent = adjoeCampaignResponse.getPromoEvent();
                        if (promoEvent.isRunningNow()) {
                            response.putString("promo_start_date", String.valueOf(promoEvent.getStartDate().getTime()));
                            response.putString("promo_end_date", String.valueOf(promoEvent.getEndDate().getTime()));
                            response.putDouble("promo_factor", promoEvent.getFactor());
                        }
                    }

                    for (AdjoePartnerApp app : adjoeCampaignResponse.getPartnerApps()) {
                        apps.pushMap(partnerAppToWritableMap(app));
                        PARTNER_APPS.put(app.getPackageName(), app);
                    }
                    response.putArray("campaigns", apps);
                    promise.resolve(response);
                }

                @Override
                public void onCampaignsReceivedError(AdjoeCampaignResponseError adjoeCampaignResponseError) {
                    if (adjoeCampaignResponseError.getException() != null) {
                        promise.reject(adjoeCampaignResponseError.getException());
                    } else {
                        promise.reject("", "");
                    }
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void requestPostInstallRewardCampaignApps(ReadableMap map, final Promise promise) {
        try {
            FrameLayout webViewContainer = null;
            if (webViewSupplier != null) {
                webViewContainer = webViewSupplier.getLayoutForWebView();
            }
            AdjoeParams params = constructAdjoeParams(map);
            Adjoe.requestPostInstallRewardPartnerApps(reactContext, webViewContainer, params, new AdjoeCampaignListener() {

                @Override
                public void onCampaignsReceived(AdjoeCampaignResponse adjoeCampaignResponse) {
                    WritableArray apps = Arguments.createArray();
                    for (AdjoePartnerApp app : adjoeCampaignResponse.getPartnerApps()) {
                        apps.pushMap(partnerAppToWritableMap(app));

                        PARTNER_APPS.put(app.getPackageName(), app);
                    }
                    WritableMap result = Arguments.createMap();
                    result.putArray("campaigns", apps);
                    promise.resolve(result);
                }

                @Override
                public void onCampaignsReceivedError(AdjoeCampaignResponseError adjoeCampaignResponseError) {
                    if (adjoeCampaignResponseError.getException() != null) {
                        promise.reject(adjoeCampaignResponseError.getException());
                    } else {
                        promise.reject("", "");
                    }
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void executePartnerAppClick(final String packageName, ReadableMap map, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        AdjoePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name " + packageName));
            return;
        }

        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        AdjoeParams params = constructAdjoeParams(map);
        partnerApp.executeClick(reactContext, webViewContainer, params, new AdjoePartnerApp.ClickListener() {

            @Override
            public void onFinished() {
                promise.resolve(null);
            }

            @Override
            public void onError() {
                promise.reject(new RuntimeException("Could not execute click for " + packageName));
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

        AdjoePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name " + packageName));
            return;
        }
        if (packageName.isEmpty()) {
            return;
        }

        try {
            final Intent launchIntent = reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                reactContext.startActivity(launchIntent);
                promise.resolve(true);
                return;
            }
        } catch (Exception ignored) {}

        try {
            final Intent marketLaunchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            marketLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            marketLaunchIntent.setPackage("com.android.vending");
            reactContext.startActivity(marketLaunchIntent);
            promise.resolve(true);
        } catch (Exception exception) {
            promise.reject(exception);
        }
    }

    @ReactMethod
    public void executePartnerAppView(final String packageName, ReadableMap map, final Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        AdjoePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name " + packageName));
            return;
        }

        FrameLayout webViewContainer = null;
        if (webViewSupplier != null) {
            webViewContainer = webViewSupplier.getLayoutForWebView();
        }
        AdjoeParams params = constructAdjoeParams(map);
        partnerApp.executeView(reactContext, webViewContainer, params, new AdjoePartnerApp.ViewListener() {

            @Override
            public void onFinished() {
                promise.resolve(null);
            }

            @Override
            public void onError() {
                promise.reject(new RuntimeException("Could not execute view for " + packageName));
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

        AdjoePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name " + packageName));
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

        AdjoePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name"));
            return;
        }

        AdjoePartnerApp.RewardLevel level = partnerApp.getNextRewardLevel(reactContext);
        if (level == null) {
            promise.reject(new NullPointerException("partner app has no next reward level"));
        } else {
            promise.resolve(rewardLevelToWritableMap(level));
        }
    }

    @ReactMethod
    public void requestInstalledPartnerApps(ReadableMap map, final Promise promise) {
        try {
            AdjoeParams params = constructAdjoeParams(map);
            Adjoe.requestInstalledPartnerApps(reactContext, params, new AdjoeCampaignListener() {

                @Override
                public void onCampaignsReceived(AdjoeCampaignResponse adjoeCampaignResponse) {
                    WritableArray apps = Arguments.createArray();
                    for (AdjoePartnerApp app : adjoeCampaignResponse.getPartnerApps()) {
                        apps.pushMap(partnerAppToWritableMap(app));

                        PARTNER_APPS.put(app.getPackageName(), app);
                    }
                    promise.resolve(apps);
                }

                @Override
                public void onCampaignsReceivedError(AdjoeCampaignResponseError adjoeCampaignResponseError) {
                    if (adjoeCampaignResponseError.getException() != null) {
                        promise.reject(adjoeCampaignResponseError.getException());
                    } else {
                        promise.reject("", "");
                    }
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getInstallDate(String packageName, Promise promise) {
        if (packageName == null) {
            promise.reject(new NullPointerException("package name must not be null"));
            return;
        }

        AdjoePartnerApp partnerApp = PARTNER_APPS.get(packageName);

        if (partnerApp == null) {
            promise.reject(new NullPointerException("no partner app found for package name " + packageName));
            return;
        }

        Date installDate = partnerApp.getInstallDate();
        if(installDate == null) {
            promise.reject(new NullPointerException("no time found for this partner app" + packageName));
            return;
        }

        promise.resolve(ISO_8601.format(installDate));
    }

    @ReactMethod
    public void _a(boolean a, final Promise promise) {
        try {
            Adjoe.a(reactContext, a);
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setTosAccepted(final Promise promise) {
        try {
            Adjoe.setTosAccepted(reactContext, new AdjoeInitialisationListener() {

                @Override
                public void onInitialisationFinished() {
                    promise.resolve(null);
                }

                @Override
                public void onInitialisationError(Exception e) {
                    promise.reject(e);
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setUsagePermissionAccepted(final Promise promise) {
        try {
            Adjoe.setUsagePermissionAccepted(reactContext, new AdjoeInitialisationListener() {

                @Override
                public void onInitialisationFinished() {
                    promise.resolve(null);
                }

                @Override
                public void onInitialisationError(Exception e) {
                    promise.reject(e);
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void requestUsagePermission(boolean bringBackAfterAccept, int ticks, final Promise promise) {
        try {
            Adjoe.requestUsagePermission(reactContext.getCurrentActivity(), new AdjoeUsageManagerCallback() {

                @Override
                public void onUsagePermissionAccepted() {
                    promise.resolve(null);
                }

                @Override
                public void onUsagePermissionError(AdjoeException e) {
                    promise.reject(e);
                }

            }, ticks, bringBackAfterAccept);
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void sendEvent(int event, String extra, ReadableMap map) {
        try {
            AdjoeParams params = constructAdjoeParams(map);
            Adjoe.sendUserEvent(reactContext, event, extra, params);
        } catch (AdjoeNotInitializedException e) {
            Log.w("RNAdjoeSDK", e);
        }
    }

    /* -----------------------------
      ADVANCED INTEGRATION METHODS END
       ----------------------------- */

    /* -----------------------------
          PHONE VERIFICATION START
       ----------------------------- */

    @ReactMethod
    public void pvInitialize(String appHash, final Promise promise) {
        try {
            phoneVerification = new AdjoePhoneVerification(appHash, new AdjoePhoneVerification.Callback() {

                @Override
                public void onError(AdjoeException e) {
                    promise.reject("0", e.getMessage(), e);
                }

                @Override
                public void onSmsTimeout() {
                    promise.reject("1", "sms_timeout");
                }

                @Override
                public void onRequestHintFailure(AdjoeException e) {
                    promise.reject("2", "request_hint_failure", e);
                }

                @Override
                public void onRequestHintNotAvailable() {
                    promise.reject("3", "request_hint_not_available");
                }

                @Override
                public void onRequestHintOtherAccount() {
                    promise.reject("4", "request_hint_other_account");
                }

                @Override
                public void onCannotExtractCode() {
                    promise.reject("5", "cannot_extract_code");
                }
            });
        } catch (AdjoeNotInitializedException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void pvSetAppHash(String appHash, Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        phoneVerification.setAppHash(appHash);
    }

    @ReactMethod
    public void pvSetCheckCallback(final Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        phoneVerification.setCheckCallback(new AdjoePhoneVerification.CheckCallback() {

            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onAlreadyVerified() {
                promise.reject("1", "already_verified");
            }

            @Override
            public void onAlreadyTaken() {
                promise.reject("2", "already_taken");
            }

            @Override
            public void onTooManyAttempts() {
                promise.reject("3", "too_many_attempts");
            }

            @Override
            public void onInvalidCountryCode() {
                promise.reject("4", "invalid_country_code");
            }

            @Override
            public void onError(AdjoeException e) {
                promise.reject("0", e.getMessage(), e);
            }
        });
    }

    @ReactMethod
    public void pvSetVerifyCallback(final Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        phoneVerification.setVerifyCallback(new AdjoePhoneVerification.VerifyCallback() {

            @Override
            public void onVerified() {
                promise.resolve(null);
            }

            @Override
            public void onInvalidCode() {
                promise.reject("1", "invalid_code");
            }

            @Override
            public void onTooManyAttempts() {
                promise.reject("2", "too_many_attempts");
            }

            @Override
            public void onMaxAllowedDevicesReached() {
                promise.reject("3", "max_allowed_devices_reached");
            }

            @Override
            public void onError(AdjoeException e) {
                promise.reject("0", e.getMessage(), e);
            }
        });
    }

    @ReactMethod
    public void pvGetStatus(final Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        phoneVerification.getStatus(reactContext, new AdjoePhoneVerification.StatusCallback() {

            @Override
            public void onVerified() {
                promise.resolve(true);
            }

            @Override
            public void onNotVerified() {
                promise.resolve(false);
            }

            @Override
            public void onError(AdjoeException e) {
                promise.reject("0", e.getMessage(), e);
            }
        });
    }

    @ReactMethod
    public void pvStartManual(String phoneNumber, Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        phoneVerification.startManual(reactContext, phoneNumber);
    }

    @ReactMethod
    public void pvVerifyCode(String code, Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        phoneVerification.verifyCode(reactContext, code);
    }

    @ReactMethod
    public void pvStartAutomatic(Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        if (phoneVerificationSupplier == null) {
            promise.reject("0", "You must call RNAdjoeSdkModule.setPhoneVerificationSupplier first");
            return;
        }

        if (phoneVerificationSupplier.getFragmentActivity() != null) {
            try {
                phoneVerification.startAutomatic(phoneVerificationSupplier.getFragmentActivity());
            } catch (AdjoeException e) {
                promise.reject(e);
            }
        } else if (phoneVerificationSupplier.getActivity() != null && phoneVerificationSupplier.getGoogleApiClient() != null) {
            try {
                phoneVerification.startAutomatic(phoneVerificationSupplier.getActivity(), phoneVerificationSupplier.getGoogleApiClient());
            } catch (AdjoeException e) {
                promise.reject(e);
            }
        } else {
            promise.reject("0", "The PhoneVerificationSupplier must return a FragmentActivity or an Activity and a GoogleApiClient");
        }
    }

    @ReactMethod
    public void pvStartAutomaticWithPhoneNumber(String phoneNumber, Promise promise) {
        if (phoneVerification == null) {
            promise.reject("0", "You must call pvInitialize first");
            return;
        }

        phoneVerification.startAutomaticWithPhoneNumber(reactContext, phoneNumber);
    }

    /* -----------------------------
           PHONE VERIFICATION END
       ----------------------------- */

    /* -----------------------------
          UTILITY METHODS START
       ----------------------------- */

    @ReactMethod
    public void getVersion(Promise promise) {
        promise.resolve(Adjoe.getVersion());
    }

    @ReactMethod
    public void getVersionName(Promise promise) {
        promise.resolve(Adjoe.getVersionName());
    }

    @ReactMethod
    public void isInitialized(Promise promise) {
        promise.resolve(Adjoe.isInitialized());
    }

    @ReactMethod
    public void hasAcceptedTOS(Promise promise) {
        promise.resolve(Adjoe.hasAcceptedTOS(reactContext));
    }

    @ReactMethod
    public void hasAcceptedUsagePermission(Promise promise) {
        promise.resolve(Adjoe.hasAcceptedUsagePermission(reactContext));
    }

    @ReactMethod
    public void getUserId(Promise promise) {
        promise.resolve(Adjoe.getUserId(reactContext));
    }

    @ReactMethod
    public void canUseOfferwallFeatures(Promise promise) {
        promise.resolve(Adjoe.canUseOfferwallFeatures(reactContext));
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
            promise.reject("0", "You must call RNAdjoeSdkModule.setPhoneVerificationSupplier first");
            return;
        }

        Activity activity = phoneVerificationSupplier.getActivity();

        if (activity == null) {
            promise.reject("0", "phoneVerificationSupplier.getActivity() == null");
            return;
        }

        try {
            Adjoe.faceVerification(activity, new Adjoe.FaceVerificationCallback() {

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
        } catch (AdjoeNotInitializedException e) {
            promise.reject("0", e.getMessage(), e);
        }
    }

    @ReactMethod
    public void faceVerificationStatus(final Promise promise) {
        try {
            Adjoe.faceVerificationStatus(reactContext, new Adjoe.FaceVerificationStatusCallback() {

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
        } catch (AdjoeNotInitializedException e) {
            promise.reject("0", e.getMessage(), e);
        }
    }

    /* -----------------------------
            FRAUD METHODS END
    ----------------------------- */

    // region helper methods
    private AdjoeParams constructAdjoeParams(ReadableMap paramsMap) {
        AdjoeParams.Builder builder = new AdjoeParams.Builder();
        if (paramsMap != null) {
            if (paramsMap.hasKey("placement"))
                builder.setPlacement(paramsMap.getString("placement"));
            if (paramsMap.hasKey("uaNetwork"))
                builder.setUaNetwork(paramsMap.getString("uaNetwork"));
            if (paramsMap.hasKey("uaChannel"))
                builder.setUaChannel(paramsMap.getString("uaChannel"));
            if (paramsMap.hasKey("uaSubPublisherCleartext"))
                builder.setUaSubPublisherCleartext(paramsMap.getString("uaSubPublisherCleartext"));
            if (paramsMap.hasKey("uaSubPublisherEncrypted"))
                builder.setUaSubPublisherEncrypted(paramsMap.getString("uaSubPublisherEncrypted"));
        }
        return builder.build();
    }

    private AdjoeExtensions constructAdjoeExtension(ReadableMap extensionMap) {
        AdjoeExtensions.Builder extensions = new AdjoeExtensions.Builder();
        if (extensionMap == null) return extensions.build();
        return extensions.setSubId1(extensionMap.getString("subId1"))
                .setSubId2(extensionMap.getString("subId2"))
                .setSubId3(extensionMap.getString("subId3"))
                .setSubId4(extensionMap.getString("subId4"))
                .setSubId5(extensionMap.getString("subId5"))
                .build();
    }

    private AdjoeUserProfile constructAdjoeUserProfile(ReadableMap userProfileMap) {
        if (userProfileMap == null) return null;
        String gender = userProfileMap.getString("gender");
        AdjoeGender adjoeGender;
        if ("male".equalsIgnoreCase(gender)) {
            adjoeGender = AdjoeGender.MALE;
        } else if ("female".equalsIgnoreCase(gender)) {
            adjoeGender = AdjoeGender.FEMALE;
        } else
            adjoeGender = AdjoeGender.UNKNOWN;

        String birthdate = userProfileMap.getString("birthday");
        Date birthday = null;
        if (!TextUtils.isEmpty(birthdate)) {
            try {
                birthday = DateFormat.getDateInstance().parse(birthdate);
            } catch (ParseException ignore) {
            }
        }
        return new AdjoeUserProfile(adjoeGender, birthday);
    }



    // endregion helper method

    private WritableMap partnerAppToWritableMap(AdjoePartnerApp app) {
        WritableMap appMap = Arguments.createMap();
        appMap.putString("campaign_type", app.getCampaignType());
        appMap.putString("name", app.getName());
        appMap.putString("package_name", app.getPackageName());
        appMap.putString("description", app.getDescription());
        appMap.putString("icon_url", app.getIconURL());
        appMap.putString("landscape_image_url", app.getLandscapeImageURL());
        appMap.putString("video_url", app.getVideoURL());
        appMap.putBoolean("in_app_purchase_enabled", app.isInAppPurchaseEnabled());
        appMap.putDouble("created_at", app.getCreatedAt() == null ? -1 : app.getCreatedAt().getTime());
        appMap.putDouble("multiplier", app.getMultiplier());
        appMap.putString("category", app.getAppCategory());
        appMap.putString("portrait_image_url", app.getPortraitImageURL());
        appMap.putString("portrait_video_url", app.getPortraitVideoURL());
        AppDetails appDetails = app.getAppDetails();
        if (appDetails != null) {
            appMap.putMap("appDetails", appDetailsToWritableMap(appDetails));
        }

        if (app.getAdvanceRewardCoins() > 0) {
            appMap.putInt("post_install_reward_event_coins", app.getAdvanceRewardCoins());
            appMap.putInt("advance_reward_coins", app.getAdvanceRewardCoins());
        }

        AdjoePromoEvent event = app.getEvent();
        if (event != null && event.isRunningNow()) {
            appMap.putString("promo_start_date", String.valueOf(event.getStartDate().getTime()));
            appMap.putString("promo_end_date", String.valueOf(event.getEndDate().getTime()));
            appMap.putDouble("promo_factor", event.getFactor());
        }

        WritableArray rewardConfig = Arguments.createArray();
        for (AdjoePartnerApp.RewardLevel level : app.getRewardConfig()) {
            rewardConfig.pushMap(rewardLevelToWritableMap(level));
        }

        appMap.putArray("reward_config", rewardConfig);
        // new
        appMap.putInt("advance_daily_limit", app.getAdvanceDailyLimit());
        appMap.putInt("advance_total_limit", app.getAdvanceTotalLimit());
        appMap.putInt("advance_plus_coins", app.getAdvancePlusCoins());
        appMap.putString("advance_plus_action_description", app.getAdvancePlusActionDescription());
        appMap.putString("advance_plus_reward_action", app.getAdvancePlusRewardedAction());

        // streak info for request app
        appMap.putBoolean("is_in_coin_streak_experiment", app.isInCoinStreakExperiment());
        appMap.putInt("coin_streak_max_coin_amount", app.getCoinStreakMaxCoinAmount());

        // streak info for rewarded
        AdjoeStreakInfo streakInfo = app.getStreakInfo();
        if (streakInfo != null) {
            WritableMap streakInfoMap = Arguments.createMap();
            streakInfoMap.putBoolean("streak_info_failed", streakInfo.isFailed());
            streakInfoMap.putInt("streak_info_last_achieved_day",streakInfo.getLastAchievedDay());
            WritableArray coinSettingsArray = Arguments.createArray();
            List<AdjoeCoinSetting> coinSettings = streakInfo.getCoinSettings();
            for (AdjoeCoinSetting coinSetting : coinSettings) {
                WritableMap coinSettingMap = Arguments.createMap();
                coinSettingMap.putInt("day", coinSetting.getDay());
                coinSettingMap.putInt("coins", coinSetting.getCoins());
                coinSettingsArray.pushMap(coinSettingMap);
            }
            streakInfoMap.putArray("coin_settings", coinSettingsArray);
            appMap.putMap("streak_info", streakInfoMap);
        }

        AdjoeAdvancePlusConfig advancePlusConfig = app.getAdvancePlusConfig();
        if (advancePlusConfig != null) {
            WritableMap advancePlusConfigMap = Arguments.createMap();
            advancePlusConfigMap.putInt("total_coins", advancePlusConfig.getTotalCoins());

            WritableArray sequentialEventsArray = Arguments.createArray();

            for (AdjoeAdvancePlusEvent sequentialEvent: advancePlusConfig.getSequentialEvents()) {
                WritableMap sequentialEventMap = Arguments.createMap();
                sequentialEventMap.putString("name", sequentialEvent.getName());
                sequentialEventMap.putString("description", sequentialEvent.getDescription());
                sequentialEventMap.putInt("coins", sequentialEvent.getCoins());

                String rewardedAt = sequentialEvent.getRewardedAt();
                if (rewardedAt != null && !rewardedAt.isEmpty()) {
                    sequentialEventMap.putString("rewarded_at", rewardedAt);
                }

                sequentialEventsArray.pushMap(sequentialEventMap);
            }

            advancePlusConfigMap.putArray("sequential_events", sequentialEventsArray);

            appMap.putMap("advance_plus_config", advancePlusConfigMap);
        }
        return appMap;
    }

    static WritableMap rewardLevelToWritableMap(AdjoePartnerApp.RewardLevel level) {
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
