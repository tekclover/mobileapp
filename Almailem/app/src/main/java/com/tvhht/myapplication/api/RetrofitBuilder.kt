package com.tvhht.myapplication.api


import com.tvhht.myapplication.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object RetrofitBuilder {
    private const val BASE_URL = BuildConfig.SERVER_URL;
    private var sOkHttpClient: OkHttpClient? = null
    private var sslContext: SSLContext? = null
    private var sslSocketFactory: javax.net.ssl.SSLSocketFactory? = null

    var interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()


    private fun getRetrofit(): Retrofit {
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY };
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
    }


    private fun getOkHttpClient(): OkHttpClient? {


        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

            override fun checkClientTrusted(
                p0: Array<out java.security.cert.X509Certificate>?,
                p1: String?
            ) {
                TODO("Not yet implemented")
            }

            override fun checkServerTrusted(
                p0: Array<out java.security.cert.X509Certificate>?,
                p1: String?
            ) {
                TODO("Not yet implemented")
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf<java.security.cert.X509Certificate>()
            }


        })

//
//        // Create an ssl socket factory with our all-trusting manager
//        if (BuildConfig.DEBUG) {
//            val sslContext = SSLContext.getInstance("SSL")
//            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
//
//            val sslSocketFactory1 = sslContext.socketFactory
//            val client = OkHttpClient.Builder()
//                .sslSocketFactory(sslSocketFactory1, trustAllCerts[0] as X509TrustManager)
//                .hostnameVerifier { _, _ -> true }.connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor { chain ->
//                    val original = chain.request()
//                    //int maxAge = 60 * 60 * 24; // tolerate 1 day
//                    val request = original.newBuilder()
//                        // .header("Content-Type", "application/json")
//                        .method(original.method, original.body)
//                        .build()
//                    chain.proceed(request)
//                }
//            client.interceptors().add(interceptor);
//
//            sOkHttpClient = client.build()
//            return sOkHttpClient
//        } else {

            sslSocketFactory = sslContext?.socketFactory
            val okHttpClientBuilder = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    //int maxAge = 60 * 60 * 24; // tolerate 1 day
                    val request = original.newBuilder()
                        // .header("Content-Type", "application/json")
                        .method(original.method, original.body)
                        .build()
                    chain.proceed(request)
                }
            okHttpClientBuilder.interceptors().add(interceptor);

            sOkHttpClient = okHttpClientBuilder.build()


            return sOkHttpClient
      //  }
    }

    val apiService: ApiServices = getRetrofit().create(ApiServices::class.java)


}