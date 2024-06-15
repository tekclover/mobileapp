import com.google.gson.annotations.SerializedName

data class ConfirmedStorageBin (

	@SerializedName("putawayConfirmedQty") val putawayConfirmedQty : Int?,
	@SerializedName("storageBin") val storageBin : String?
)