package com.tvhht.myapplication.api


import com.tvhht.myapplication.home.model.*
import com.tvhht.myapplication.login.model.*

import retrofit2.http.*
import rx.Observable


interface ApiServices {

    /*Login*/
    @POST("auth-token/")
    fun checkUser(@Body loginModel: LoginModel): Observable<LoginResponse?>

    @GET("wms-idmaster-service/hhtuser")
    fun getUserDetails(@Query("authToken") token: String?): Observable<List<UserDetails?>>

    @GET("wms-idmaster-service/usermanagement/{userId}")
    fun getWarehousseDetails(
        @Path("userId") userId: String?,
        @Query("authToken") token: String?,
        @Query("warehouseId") warehouseId: String?
    ): Observable<WarehouseModel?>

    @GET("wms-idmaster-service/login")
    fun getLoginDetails(
        @Query("authToken") token: String?,
        @Query("name") name: String?,
        @Query("password") password: String?,
        @Query("version") version: String?
    ): Observable<UserLoginResponse>

    /*Barcode*/
    @POST("wms-idmaster-service/interimbarcode")
    fun getBarcodeStatus(
        @Query("authToken") token: String?,
        @Query("loginUserID") id: String?,
        @Body postData: BarcodeRequestBin
    ): Observable<BarcodeResponse?>




    /*HOME SCREEN COUNTS*/
    @GET("wms-transaction-service/reports/dashboard/mobile")
    fun getDasboardCount(
        @Query("authToken") token: String?,
        @Query("warehouseId") warehouseId: String?
    ): Observable<DashBoardReportCount>


    @GET("wms-masters-service/imbasicdata1/findItemCodeByLikeNew")
    fun getItemCodeLikes(
        @Query("authToken") token: String?,
        @Query("companyCodeId") companyCodeId: String?,
        @Query("languageId") languageId: String?,
        @Query("likeSearchByItemCodeNDesc") likeSearchByItemCodeNDesc: String?,
        @Query("plantId") plantId: String?,
        @Query("warehouseId") warehouseId: String?
    ): Observable<List<ItemCodeResponse>>

    @POST("wms-masters-service/imbasicdata1/findImBasicData1New")
    fun getManufactureDetails(
        @Query("authToken") token: String?,
        @Body postData: ManufactureRequest
    ): Observable<List<ManufactureResponse>?>


}