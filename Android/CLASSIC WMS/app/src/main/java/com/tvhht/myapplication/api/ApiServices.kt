package com.tvhht.myapplication.api

import com.tvhht.myapplication.annual.model.*
import com.tvhht.myapplication.cases.model.AsnList
import com.tvhht.myapplication.cases.model.CaseConfirmResponse
import com.tvhht.myapplication.cases.model.CasesConfirmRequest
import com.tvhht.myapplication.home.model.DashBoardReportCount
import com.tvhht.myapplication.login.model.*
import com.tvhht.myapplication.picking.model.*
import com.tvhht.myapplication.putaway.model.*
import com.tvhht.myapplication.quality.model.*
import com.tvhht.myapplication.reports.model.ReportRequestBin
import com.tvhht.myapplication.reports.model.ReportRequestItemCode
import com.tvhht.myapplication.reports.model.ReportRequestPalletID
import com.tvhht.myapplication.reports.model.ReportResponse
import com.tvhht.myapplication.stock.model.*
import com.tvhht.myapplication.transfers.model.*
import retrofit2.Response
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

    /*CASES*/
    @GET("wms-transaction-service/stagingline")
    fun getAsnList(@Query("authToken") token: String?): Observable<List<AsnList?>>

/*    *//*CASES*//*
    @GET("wms-transaction-service/stagingline/{lineNo}/caseConfirmation")
    fun updateCases(
        @Path("lineNo") lineNo: Int?,
        @Query("authToken") token: String?,
        @Query("caseCode") caseCode: String?,
        @Query("itemCode") itemCode: String?,
        @Query("loginUserID") loginUserID: String?,
        @Query("palletCode") palletCode: String?,
        @Query("preInboundNo") preInboundNo: String?,
        @Query("refDocNumber") refDocNumber: String?,
        @Query("stagingNo") stagingNo: String?,
        @Query("warehouseId") warehouseId: String?
    ): Observable<CaseConfirmResponse?>*/


    @PATCH("wms-transaction-service/stagingline/caseConfirmation")
    fun updateCases(
        @Query("authToken") token: String?,
        @Query("caseCode") caseCode: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: List<CasesConfirmRequest?>?
    ): Observable<List<CaseConfirmResponse?>>

    /*PUTAWAY*/
    @GET("wms-transaction-service/putawayheader")
    fun getAllPutAway(@Query("authToken") token: String?): Observable<List<PutAwayModel?>>

    /*PUTAWAY*/
    @POST("wms-transaction-service/putawayheader/findPutAwayHeader")
    fun getAllPutAwayNew(@Query("authToken") token: String?,  @Body postData: PutAwayHeader): Observable<List<PutAwayModel?>>


    /*PUTAWAY*/
    @POST("wms-transaction-service/putawayline")
    fun createPutAway(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: List<PutAwaySubmit?>?
    ): Observable<List<PutAwayConfirmResponse>>

    /*PUTAWAY*/
    @POST("wms-transaction-service/findGrLine")
    fun findSearch(
        @Query("authToken") token: String?,
        @Body postData: SearchGR
    ): Observable<List<PutAwayMfrModel?>>

    /*TRANSFERS*/
    @POST("wms-transaction-service/inventory/findInventory")
    fun findInventoryDetails(
        @Query("authToken") token: String?,
        @Body postData: SearchInventory
    ): Observable<List<InventoryModel?>>

    /*TRANSFERS*/
    @POST("wms-masters-service/storagebin/findStorageBin")
    fun findTargetBin(
        @Query("authToken") token: String?,
        @Body postData: SearchTargetBin
    ): Observable<List<TargetBinResponse?>>


    /*TRANSFERS*/
    @POST("wms-transaction-service/inhousetransferheader")
    fun createTransferInventory(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: CreateTransferRequest
    ): Observable<CreateTransferRequest?>


    /*PICKING*/
    @POST("wms-transaction-service/pickupheader/findPickupHeader")
    fun findPickUpList(
        @Query("authToken") token: String?,
        @Body postData: PickUpHeader
    ): Observable<List<PickingListResponse?>>


    /*PICKING*/
    @POST("wms-transaction-service/inventory/findInventory")
    fun getInventoryQty(
        @Query("authToken") token: String?,
        @Body postData: PickingInventory
    ): Observable<List<InventoryModel?>>


    /*REPORTS*/
    @POST("wms-transaction-service/inventory/findInventory")
    fun getReportsFromPalletID(
        @Query("authToken") token: String?,
        @Body postData: ReportRequestPalletID
    ): Observable<List<ReportResponse?>>


    /*REPORTS*/
    @POST("wms-transaction-service/inventory/findInventory")
    fun getReportsFromBinID(
        @Query("authToken") token: String?,
        @Body postData: ReportRequestBin
    ): Observable<List<ReportResponse?>>

    /*REPORTS*/
    @POST("wms-transaction-service/inventory/findInventory")
    fun getReportsItemCode(
        @Query("authToken") token: String?,
        @Body postData: ReportRequestItemCode
    ): Observable<List<ReportResponse?>>


    /*REPORTS*/
    @POST("wms-transaction-service/findGrLine")
    fun findMfrDetails(
        @Query("authToken") token: String?,
        @Body postData: SearchMfr
    ): Observable<List<PickingMfrModel?>>


    /*REPORTS*/
    @GET("wms-transaction-service/pickupline/additionalBins")
    fun getAdditionalBin(
        @Query("authToken") token: String?,
        @Query("itemCode") itemCode: String?,
        @Query("obOrdertypeId") orderId: Int?,
        @Query("proposedPackBarCode") proposedPackBarCode: String?,
        @Query("proposedStorageBin") proposedStorageBin: String?,
        @Query("warehouseId") warehouseId: String?
    ): Observable<List<BinResponse?>>


    /*REPORTS*/
    @POST("wms-transaction-service/pickupline")
    fun createPickingRecords(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: List<PickingSubmitRequest>
    ): Observable<List<PickingSubmitResponse>>


    /*QUALITY*/
    @POST("wms-transaction-service/qualityheader/findQualityHeader")
    fun getQualityList(
        @Query("authToken") token: String?,
        @Body postData: QualityRequest
    ): Observable<List<QualityListResponse>>

    /*QUALITY*/
    @POST("wms-transaction-service/pickupline/findPickupLine")
    fun getQualityListDetails(
        @Query("authToken") token: String?,
        @Body postData: QualityDetailRequestModel
    ): Observable<List<QualityDetailsModel>>


    /*QUALITY*/
    @POST("wms-transaction-service/qualityline")
    fun createQuality(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: List<QualityModel>
    ): Observable<List<QualityResponse>>


    /*HOME SCREEN COUNTS*/
    @GET("wms-transaction-service/reports/dashboard/mobile")
    fun getDasboardCount(
        @Query("authToken") token: String?,
        @Query("warehouseId") warehouseId: String?
    ): Observable<DashBoardReportCount>


    /*DESCRIPTION*/
    @POST("wms-masters-service/imbasicdata1/findImBasicData1")
    fun findItemSescription(
        @Query("authToken") token: String?,
        @Body postData: SearchDescription
    ): Observable<List<DescriptionModel?>>


//    /*STOCKS*/
//    @POST("wms-transaction-service/perpetualheader/findPerpetualHeader")
//    fun findPerpetualStockList(
//        @Query("authToken") token: String?,
//        @Body postData: PerpetualHeader
//    ): Observable<List<PerpetualResponse?>>

    /*STOCKS*/
    @POST("wms-transaction-service/findPerpetualLine")
    fun findPerpetualStockList(
        @Query("authToken") token: String?,
        @Body postData: PerpetualHeader
    ): Observable<List<PerpetualResponse?>>


    /*STOCKS*/
    @POST("wms-transaction-service/findPerpetualLine")
    fun findPerpetualStockDetailList(
        @Query("authToken") token: String?,
        @Body postData: AnnualDetailHeader
    ): Observable<List<PerpetualLine>>


    /*DESCRIPTION*/
    @GET("wms-masters-service/handlingequipment/{heBarcode}/barCode")
    fun findHandlingEquipment(
        @Path("heBarcode") heBarcode: String?,
        @Query("authToken") token: String?,
        @Query("warehouseId") warehouseId: String?
    ): Observable<HEResponse>


    /*STOCKS*/
    @PATCH("wms-transaction-service/perpetualheader/{cycleCountNo}")
    fun updatePerpetualStockList(
        @Path("cycleCountNo") cycleCountNo: String?,
        @Query("authToken") authToken: String?,
        @Query("cycleCountTypeId") cycleCountTypeId: Int?,
        @Query("loginUserID") loginUserID: String?,
        @Query("movementTypeId") movementTypeId: Int?,
        @Query("subMovementTypeId") subMovementTypeId: Int?,
        @Query("warehouseId") warehouseId: String?,
        @Body postData: StockPerpetualRequest
    ): Observable<Response<Unit>??>


//    /*STOCKS*/
//    @POST("wms-transaction-service/periodicline/findPeriodicLine")
//    fun findAnnualStockList(
//        @Query("authToken") token: String?,
//        @Query("pageNo") pageNo: Int?,
//        @Query("pageSize") pageSize: Int?,
//        @Query("sortBy") sortBy: String?,
//        @Body postData: AnnualHeader
//    ): Observable<List<AnnualStockResponse?>>
    /*STOCKS*/
    @POST("wms-transaction-service/periodicline/findPeriodicLine")
    fun findAnnualStockList(
        @Query("authToken") token: String?,
        @Body postData: AnnualHeader
    ): Observable<List<AnnualStockResponse?>>

    /*STOCKS*/
    @POST("wms-transaction-service/periodicline/findPeriodicLine")
    fun findAnnualStockListDetails(
        @Query("authToken") token: String?,
        @Body postData: AnnualDetailHeader1
    ): Observable<List<PeriodicLine>>


    /*STOCKS*/
    @PATCH("wms-transaction-service/periodicheader/{cycleCountNo}")
    fun updateAnnualStockList(
        @Path("cycleCountNo") cycleCountNo: String?,
        @Query("authToken") authToken: String?,
        @Query("cycleCountTypeId") cycleCountTypeId: Int?,
        @Query("loginUserID") loginUserID: String?,
        @Query("warehouseId") warehouseId: String?,
        @Body postData: AnnualStockRequest
    ): Observable<AnnualStockResponse?>


}