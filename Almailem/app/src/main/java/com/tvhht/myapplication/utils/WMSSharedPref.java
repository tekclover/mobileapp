package com.tvhht.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tvhht.myapplication.annual.model.AnnualStockResponse;
import com.tvhht.myapplication.annual.model.PeriodicLine;
import com.tvhht.myapplication.cases.model.CaseList;
import com.tvhht.myapplication.home.model.DashBoardReportCount;
import com.tvhht.myapplication.login.model.UserDetails;
import com.tvhht.myapplication.picking.model.PickingCombineResponse;
import com.tvhht.myapplication.picking.model.PickingListResponse;
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm;
import com.tvhht.myapplication.putaway.model.BinQuantityConfirm;
import com.tvhht.myapplication.putaway.model.PutAwayCombineModel;
import com.tvhht.myapplication.quality.model.QualityDetailsModel;
import com.tvhht.myapplication.quality.model.QualityListResponse;
import com.tvhht.myapplication.stock.model.PerpetualLine;
import com.tvhht.myapplication.stock.model.PerpetualResponse;
import com.tvhht.myapplication.transfers.model.CreateTransferRequest;
import com.tvhht.myapplication.transfers.model.InventoryModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class WMSSharedPref {
    @Nullable
    private static WMSSharedPref appPrefrence = null;
    @NonNull
    private final Context mContext;
    private Gson gson;
    @Nullable
    private SharedPreferences preferences = null;

    private WMSSharedPref(@NonNull Context context) {
        preferences =
                context.getSharedPreferences("WMS_SHARED_PREF", Context.MODE_PRIVATE);
        this.mContext = context;
    }

    public static WMSSharedPref getAppPrefs(@NonNull Context context) {
        synchronized (WMSSharedPref.class) {
            if (appPrefrence == null) appPrefrence = new WMSSharedPref(context);
        }

        return appPrefrence;
    }

    public void saveStringValue(@NonNull String key, String value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putString(key, value);
        editor.apply();

    }

    public void saveStringSet(@NonNull String key, Set<String> setValue) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putStringSet(key, setValue);
        editor.apply();
    }


    public Set<String> getStringSet(@NonNull String key) {
        return Objects.requireNonNull(preferences).getStringSet(key, null);
    }

    public int getIntValue(String key) {
        return Objects.requireNonNull(preferences).getInt(key, -1);
    }

    public void saveIntValue(String key, int value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveLongValue(String key, Long value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLongValue(String key) {
        return Objects.requireNonNull(preferences).getLong(key, -1);
    }

    public void putDouble(final String key, final double value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.apply();
    }

    public double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(Objects.requireNonNull(preferences).getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public String getStringValue(String key) {
        if (null == preferences)
            return "";
        return preferences.getString(key, "");
    }

    public void saveBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return Objects.requireNonNull(preferences).getBoolean(key, false);
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    //To save Hash Map
    public void saveMap(Map<String, String> inputMap, String key) {

        if (preferences != null && inputMap != null) {
            String hashMapString = getGson().toJson(inputMap);
            //save in shared prefs
            preferences.edit().putString(key, hashMapString).apply();
        }
    }

    public Map<String, String> getMap(String key) {
        Map<String, String> outputMap = new HashMap<>();
        try {
            //get from shared prefs
            String storedHashMapString = preferences.getString(key, (new JSONObject()).toString());
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            return getGson().fromJson(storedHashMapString, type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    /**
     * Save and get ArrayList in SharedPreference
     */

    public void saveArrayList(List<String> list, String key) {
        if (list != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(list);
            editor.putString(key, json);
            editor.apply();
        }
    }

    public ArrayList<String> getArrayList(String key) {
        String json = preferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return getGson().fromJson(json, type);
    }


    public void clearSharedPrefs(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clearAllSharedPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveUserInfoVO(List<UserDetails> userList) {
        if (userList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(userList);
            editor.putString("USER_DETAILS_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public List<UserDetails> getUserInfo() {
        String json = Objects.requireNonNull(preferences).getString("USER_DETAILS_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<ArrayList<UserDetails>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveUserInfoVOSingle(UserDetails userList) {
        if (userList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(userList);
            editor.putString("USER_DETAILS_LIST_SINGLE", json);
            editor.apply();
        }
    }

    @Nullable
    public UserDetails getUserInfoSingle() {
        String json = Objects.requireNonNull(preferences).getString("USER_DETAILS_LIST_SINGLE", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<UserDetails>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveCountInfo(DashBoardReportCount userList) {
        if (userList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(userList);
            editor.putString("USER_COUNT", json);
            editor.apply();
        }
    }

    @Nullable
    public DashBoardReportCount getCountInfo() {
        String json = Objects.requireNonNull(preferences).getString("USER_COUNT", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<DashBoardReportCount>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }

    public void saveCaseVO(List<CaseList> userList) {
        if (userList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(userList);
            editor.putString("CASES_DETAILS_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public List<CaseList> getCaseInfo() {
        String json = Objects.requireNonNull(preferences).getString("CASES_DETAILS_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<ArrayList<CaseList>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void savePutAwayInfo(PutAwayCombineModel putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PUTAWAY_COMB_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public PutAwayCombineModel getPutAwayInfo() {
        String json = Objects.requireNonNull(preferences).getString("PUTAWAY_COMB_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PutAwayCombineModel>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void savePickingInfo(PickingListResponse putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PICKING_COMB_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public PickingListResponse getPickingInfo() {
        String json = Objects.requireNonNull(preferences).getString("PICKING_COMB_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PickingListResponse>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveStockInfo(List<PerpetualLine> putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PERPETUAL_COMB_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public List<PerpetualLine> getStockInfo() {
        String json = Objects.requireNonNull(preferences).getString("PERPETUAL_COMB_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<List<PerpetualLine>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveAnnualStockInfo(List<PeriodicLine> putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PERPETUAL_COMB_LIST_AA", json);
            editor.apply();
        }
    }

    @Nullable
    public List<PeriodicLine> getAnnualStockInfo() {
        String json = Objects.requireNonNull(preferences).getString("PERPETUAL_COMB_LIST_AA", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<List<PeriodicLine>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveStockInfo1(PerpetualResponse putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PERPETUAL_COMB_LIST1", json);
            editor.apply();
        }
    }

    @Nullable
    public PerpetualResponse getStockInfo1() {
        String json = Objects.requireNonNull(preferences).getString("PERPETUAL_COMB_LIST1", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PerpetualResponse>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveStockInfo2(AnnualStockResponse putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PERPETUAL_COMB_LIST1_AB", json);
            editor.apply();
        }
    }

    @Nullable
    public AnnualStockResponse getStockInfo2() {
        String json = Objects.requireNonNull(preferences).getString("PERPETUAL_COMB_LIST1_AB", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<AnnualStockResponse>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }

    public void saveSingleStockAnnual(PeriodicLine putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PERPETUAL_COMB_LIST1_AB_SINGLE_VV", json);
            editor.apply();
        }
    }

    @Nullable
    public PeriodicLine getSingleStockAnnual() {
        String json = Objects.requireNonNull(preferences).getString("PERPETUAL_COMB_LIST1_AB_SINGLE_VV", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PeriodicLine>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveSingleStock(PerpetualLine putawayList) {
        if (putawayList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(putawayList);
            editor.putString("PERPETUAL_COMB_LIST1_AB_SINGLE", json);
            editor.apply();
        }
    }

    @Nullable
    public PerpetualLine getSingleStock() {
        String json = Objects.requireNonNull(preferences).getString("PERPETUAL_COMB_LIST1_AB_SINGLE", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PerpetualLine>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveInventoryInfo(InventoryModel modelList) {
        if (modelList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(modelList);
            editor.putString("INVENTORY_COMB_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public InventoryModel getInventoryInfo() {
        String json = Objects.requireNonNull(preferences).getString("INVENTORY_COMB_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<InventoryModel>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveQTYInfo(BinQuantityConfirm qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("QUANTITY_INFO_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public BinQuantityConfirm getQTYInfo() {
        String json = Objects.requireNonNull(preferences).getString("QUANTITY_INFO_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<BinQuantityConfirm>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveListPickingQTYInfo(List<PickingQuantityConfirm> qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("PICKING_QUANTITY_INFO_LIST_MUL", json);
            editor.apply();
        }
    }

    @Nullable
    public List<PickingQuantityConfirm> getListPickingQTYInfo() {
        String json = Objects.requireNonNull(preferences).getString("PICKING_QUANTITY_INFO_LIST_MUL", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<List<PickingQuantityConfirm>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void savePickingQTYInfo(PickingQuantityConfirm qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("PICKING_QUANTITY_INFO_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public PickingQuantityConfirm getPickingQTYInfo() {
        String json = Objects.requireNonNull(preferences).getString("PICKING_QUANTITY_INFO_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PickingQuantityConfirm>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveQualityInfo(QualityDetailsModel qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("QUALITY_INFO_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public QualityDetailsModel getQualityInfo() {
        String json = Objects.requireNonNull(preferences).getString("QUALITY_INFO_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<QualityDetailsModel>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveStocksQualityInfo(PerpetualLine qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("STOCKS_QUALITY_INFO_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public PerpetualLine getStocksQualityInfo() {
        String json = Objects.requireNonNull(preferences).getString("STOCKS_QUALITY_INFO_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PerpetualLine>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveStocksQualityInfo1(PeriodicLine qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("STOCKS_QUALITY_INFO_LIST_PER", json);
            editor.apply();
        }
    }

    @Nullable
    public PeriodicLine getStocksQualityInfo1() {
        String json = Objects.requireNonNull(preferences).getString("STOCKS_QUALITY_INFO_LIST_PER", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PeriodicLine>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveStocksQualityInfo2(PeriodicLine qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("STOCKS_QUALITY_INFO_LIST_PER2", json);
            editor.apply();
        }
    }

    @Nullable
    public PeriodicLine getStocksQualityInfo2() {
        String json = Objects.requireNonNull(preferences).getString("STOCKS_QUALITY_INFO_LIST_PER2", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<PeriodicLine>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveListQualityInfo(List<QualityListResponse> qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("QUALITY_INFO_LIST_DT", json);
            editor.apply();
        }
    }


    public void saveInventoryInfo(CreateTransferRequest qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("Inventory_INFO_LIST_DT", json);
            editor.apply();
        }
    }

    @Nullable
    public CreateTransferRequest getInventoryObjectInfo() {
        String json = Objects.requireNonNull(preferences).getString("Inventory_INFO_LIST_DT", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<CreateTransferRequest>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }

    @Nullable
    public List<QualityListResponse> getListQualityInfo() {
        String json = Objects.requireNonNull(preferences).getString("QUALITY_INFO_LIST_DT", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<List<QualityListResponse>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void savePickingAmount(List<PickingQuantityConfirm> qtyList) {
        if (qtyList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(qtyList);
            editor.putString("PICK_INFO_LIST_DT", json);
            editor.apply();
        }
    }

    @Nullable
    public List<PickingQuantityConfirm> getPickingAmount() {
        String json = Objects.requireNonNull(preferences).getString("PICK_INFO_LIST_DT", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<List<PickingQuantityConfirm>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }





}
