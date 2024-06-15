package com.tvhht.myapplication.api

import com.tvhht.myapplication.annual.model.*
import com.tvhht.myapplication.cases.model.AsnList
import com.tvhht.myapplication.cases.model.CaseConfirmResponse
import com.tvhht.myapplication.cases.model.CasesConfirmRequest
import com.tvhht.myapplication.goodsreceipt.model.CBMRequest
import com.tvhht.myapplication.goodsreceipt.model.CBMResponse
import com.tvhht.myapplication.goodsreceipt.model.CreateImPartnerRequest
import com.tvhht.myapplication.goodsreceipt.model.CreateImPartnerResponse
import com.tvhht.myapplication.goodsreceipt.model.FindImPartnerRequest
import com.tvhht.myapplication.goodsreceipt.model.FindImPartnerResponse
import com.tvhht.myapplication.goodsreceipt.model.GRLineSubmitResponse
import com.tvhht.myapplication.goodsreceipt.model.GoodsReceiptRequest
import com.tvhht.myapplication.goodsreceipt.model.GoodsReceiptResponse
import com.tvhht.myapplication.goodsreceipt.model.HHTUserRequest
import com.tvhht.myapplication.goodsreceipt.model.PackBarcodeResponse
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentRequest
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.goodsreceipt.model.StagingLineUpdateResponse
import com.tvhht.myapplication.home.model.DashBoardReportCount
import com.tvhht.myapplication.home.model.DashBoardRequest
import com.tvhht.myapplication.login.model.*
import com.tvhht.myapplication.picking.model.*
import com.tvhht.myapplication.pushnotification.FCMTokenRequest
import com.tvhht.myapplication.pushnotification.FCMTokenResponse
import com.tvhht.myapplication.putaway.model.*
import com.tvhht.myapplication.quality.model.*
import com.tvhht.myapplication.reports.model.FindItemCodeRequest
import com.tvhht.myapplication.reports.model.FindItemCodeResponse
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

    @POST("wms-idmaster-service/hhtuser/findHhtUser")
    fun getUserDetails(
        @Query("authToken") token: String?,
        @Body request: FindHhtUserRequest
    ): Observable<List<UserDetails?>>

    @GET("wms-idmaster-service/usermanagement/{userId}")
    fun getWarehousseDetails(
        @Path("userId") userId: String?,
        @Query("authToken") token: String?,
        @Query("warehouseId") warehouseId: String?,
        @Query("companyCodeId") companyCodeId: String?,
        @Query("plantId") plantId: String?,
        @Query("languageId") languageId: String?
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
    @POST("wms-transaction-service/putawayheader/findPutAwayHeader/v2")
    fun getAllPutAwayNew(
        @Query("authToken") token: String?,
        @Body postData: PutAwayHeader
    ): Observable<List<PutAwayModel?>>


    /*PUTAWAY*/
    @POST("wms-transaction-service/putawayline/v2")
    fun createPutAway(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: List<PutAwaySubmit?>?
    ): Observable<List<PutAwayConfirmResponse>>

    /*PUTAWAY*/
    @POST("wms-transaction-service/grline/findGrLine/v2")
    fun findSearch(
        @Query("authToken") token: String?,
        @Body postData: SearchGR
    ): Observable<List<PutAwayMfrModel?>>

    /*TRANSFERS*/
    @POST("wms-transaction-service/inventory/findInventory/v2")
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
    @POST("wms-transaction-service/inhousetransferheader/v2")
    fun createTransferInventory(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: CreateTransferRequest
    ): Observable<CreateTransferRequest?>


    /*PICKING*/
    @POST("wms-transaction-service/pickupheader/v2/findPickupHeader")
    fun findPickUpList(
        @Query("authToken") token: String?,
        @Body postData: PickUpHeader
    ): Observable<List<PickingListResponse?>>


    /*PICKING*/
    @POST("wms-transaction-service/inventory/findInventory/v2")
    fun getInventoryQty(
        @Query("authToken") token: String?,
        @Body postData: PickingInventory
    ): Observable<List<InventoryModel>>


    /*REPORTS*/
    @POST("wms-transaction-service/inventory/findInventory/v2")
    fun getReportsFromPalletID(
        @Query("authToken") token: String?,
        @Body postData: ReportRequestPalletID
    ): Observable<List<ReportResponse?>>


    /*REPORTS*/
    @POST("wms-transaction-service/inventory/findInventory/v2")
    fun getReportsFromBinID(
        @Query("authToken") token: String?,
        @Body postData: ReportRequestBin
    ): Observable<List<ReportResponse?>>

    /*REPORTS*/
    @POST("wms-transaction-service/inventory/findInventory/v2")
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
    @GET("wms-transaction-service/pickupline/v2/additionalBins")
    fun getAdditionalBin(
        @Query("authToken") token: String?,
        @Query("itemCode") itemCode: String?,
        @Query("obOrdertypeId") orderId: Int?,
        @Query("proposedPackBarCode") proposedPackBarCode: String?,
        @Query("proposedStorageBin") proposedStorageBin: String?,
        @Query("warehouseId") warehouseId: String?,
        @Query("companyCodeId") companyCodeId: String?,
        @Query("plantId") plantId: String?,
        @Query("languageId") languageId: String?,
        @Query("manufacturerName") manufacturerName: String?
    ): Observable<List<BinResponse?>>


    /*REPORTS*/
    @POST("wms-transaction-service/v2/pickupline")
    fun createPickingRecords(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: List<PickingSubmitRequest>
    ): Observable<List<PickingSubmitResponse>>


    /*QUALITY*/
    @POST("wms-transaction-service/qualityheader/v2/findQualityHeader")
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
    @POST("wms-transaction-service/v2/qualityline")
    fun createQuality(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String?,
        @Body postData: List<QualityModel>
    ): Observable<List<QualityResponse>>


    /*HOME SCREEN COUNTS*/
    @POST("wms-transaction-service/reports/dashboard/mobile/find")
    fun getDashBoardCount(
        @Query("authToken") token: String?,
        @Body request: DashBoardRequest
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
    @POST("wms-transaction-service/perpetualheader/v2/findPerpetualHeader")
    fun findPerpetualStockList(
        @Query("authToken") token: String?,
        @Body postData: AnnualHeader
    ): Observable<List<PerpetualResponse?>>


    /*STOCKS*/
    @POST("wms-transaction-service/v2/findPerpetualLine")
    fun findPerpetualStockDetailList(
        @Query("authToken") token: String?,
        @Body postData: AnnualDetailHeader
    ): Observable<List<PerpetualLine>>


    /*DESCRIPTION*/
    @GET("wms-masters-service/handlingequipment/{heBarcode}/v2/barCode")
    fun findHandlingEquipment(
        @Path("heBarcode") heBarcode: String?,
        @Query("authToken") token: String?,
        @Query("warehouseId") warehouseId: String?,
        @Query("companyCodeId") companyCodeId: String?,
        @Query("plantId") plantId: String?,
        @Query("languageId") languageId: String?
    ): Observable<HEResponse>


    /*STOCKS*/
    @PATCH("wms-transaction-service/perpetualheader/v2/{cycleCountNo}")
    fun updatePerpetualStockList(
        @Path("cycleCountNo") cycleCountNo: String?,
        @Query("authToken") authToken: String?,
        @Query("cycleCountTypeId") cycleCountTypeId: Int?,
        @Query("loginUserID") loginUserID: String?,
        @Query("movementTypeId") movementTypeId: Int?,
        @Query("subMovementTypeId") subMovementTypeId: Int?,
        @Query("warehouseId") warehouseId: String?,
        @Query("companyCodeId") companyCodeId: String?,
        @Query("plantId") plantId: String?,
        @Query("languageId") languageId: String?,
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
    @POST("wms-transaction-service/periodicheader/v2/findPeriodicHeader")
    fun findAnnualStockList(
        @Query("authToken") token: String?,
        @Body postData: AnnualHeader
    ): Observable<List<AnnualStockResponse?>>

    /*STOCKS*/
    @POST("wms-transaction-service/periodicline/v2/findPeriodicLine")
    fun findAnnualStockListDetails(
        @Query("authToken") token: String?,
        @Body postData: AnnualDetailHeader1
    ): Observable<List<PeriodicLine>>


    /*STOCKS*/
    @PATCH("wms-transaction-service/periodicheader/v2/{cycleCountNo}")
    fun updateAnnualStockList(
        @Path("cycleCountNo") cycleCountNo: String?,
        @Query("authToken") authToken: String?,
        @Query("cycleCountTypeId") cycleCountTypeId: Int?,
        @Query("loginUserID") loginUserID: String?,
        @Query("warehouseId") warehouseId: String?,
        @Query("companyCode") companyCodeId: String?,
        @Query("plantId") plantId: String?,
        @Query("languageId") languageId: String?,
        @Body postData: AnnualStockRequest
    ): Observable<AnnualStockResponse?>

    /*GOODS RECEIPT*/
    @POST("wms-transaction-service/grheader/findGrHeader/v2")
    fun getGoodsReceipt(
        @Query("authToken") token: String?,
        @Body request: GoodsReceiptRequest
    ): Observable<List<GoodsReceiptResponse>>

    /*GOODS RECEIPT*/
    @POST("wms-transaction-service/stagingline/findStagingLine/v2")
    fun getSelectedDocument(
        @Query("authToken") token: String?,
        @Body request: GoodsReceiptRequest
    ): Observable<List<SelectedDocumentResponse>>

    /*GOODS RECEIPT*/
    @POST("wms-masters-service/imbasicdata1/findImBasicData1New")
    fun getCbmDetails(
        @Query("authToken") token: String?,
        @Body request: CBMRequest
    ): Observable<List<CBMResponse>>

    /*GOODS RECEIPT*/
    @POST("wms-transaction-service/grline/v2")
    fun grLineSubmit(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String,
        @Body request: List<SelectedDocumentResponse>
    ): Observable<List<GRLineSubmitResponse>>

    /*GOODS RECEIPT*/
    @GET("wms-transaction-service/grline/packBarcode/v2")
    fun getPackBarCode(
        @Query("authToken") token: String?,
        @Query("acceptQty") acceptQty: Int?,
        @Query("damageQty") damageQty: Int?,
        @Query("warehouseId") warehouseId: String,
        @Query("companyCodeId") companyCodeId: String?,
        @Query("plantId") plantId: String?,
        @Query("languageId") languageId: String,
        @Query("loginUserID") loginUserID: String
    ): Observable<List<PackBarcodeResponse>>

    /*GOODS RECEIPT*/
    @POST("wms-idmaster-service/hhtuser/findHhtUser")
    fun getHHTUser(
        @Query("authToken") token: String?,
        @Body request: HHTUserRequest
    ): Observable<List<UserDetails?>>

    @POST("wms-masters-service/imbasicdata1/v2/findItemCodeByLikeNew")
    fun findItemCode(
        @Query("authToken") token: String?,
        @Body request: FindItemCodeRequest
    ): Observable<List<FindItemCodeResponse>>

    @GET("wms-masters-service/storagebin/v2/{storageBin}")
    fun findStorageBin(
        @Path("storageBin") storageBin: String,
        @Query("authToken") token: String?,
        @Query("companyCodeId") companyCodeId: String?,
        @Query("languageId") languageId: String,
        @Query("warehouseId") warehouseId: String,
        @Query("plantId") plantId: String?,
    ): Observable<StorageBinResponse>

    @POST("wms-masters-service/impartner/findImPartner")
    fun findImPartner(
        @Query("authToken") token: String?,
        @Body request: FindImPartnerRequest
    ): Observable<List<FindImPartnerResponse>>

    @POST("wms-masters-service/impartner")
    fun createImPartner(
        @Query("authToken") token: String?,
        @Query("loginUserID") loginUserID: String,
        @Body request: List<CreateImPartnerRequest>
    ): Observable<List<CreateImPartnerResponse>>

    @PATCH("wms-transaction-service/stagingline/{lineNo}/v2")
    fun updateStagingLine(
        @Path("lineNo") lineNo: Int?,
        @Query("authToken") token: String?,
        @Query("caseCode") caseCode: String?,
        @Query("refDocNumber") refDocNumber: String?,
        @Query("stagingNo") stagingNo: String?,
        @Query("itemCode") itemCode: String?,
        @Query("palletCode") palletCode: String?,
        @Query("preInboundNo") preInboundNo: String?,
        @Query("warehouseId") warehouseId: String?,
        @Query("companyCode") companyCode: String?,
        @Query("languageId") languageId: String?,
        @Query("plantId") plantId: String?,
        @Query("loginUserID") loginUserID: String,
        @Body request: SelectedDocumentResponse
    ): Observable<StagingLineUpdateResponse>

    @PATCH("wms-transaction-service/pickupline/v2/barcodeId")
    fun addNewBarcode(
        @Query("authToken") token: String?,
        @Body request: AddNewBarcodeRequest
    ): Observable<AddNewBarcodeResponse>

    @POST("wms-transaction-service/outboundline/v2/findOutboundLine")
    fun findOutboundLine(
        @Query("authToken") token: String?,
        @Body request: OutboundLineRequest
    ): Observable<List<OutboundLineResponse>>

    @POST("wms-idmaster-service/hhtnotification/createnotification")
    fun sendFCMTokenToServer(
        @Query("authToken") token: String,
        @Query("loginUserID") loginUserId: String,
        @Body request: FCMTokenRequest
    ): Observable<FCMTokenResponse>
}