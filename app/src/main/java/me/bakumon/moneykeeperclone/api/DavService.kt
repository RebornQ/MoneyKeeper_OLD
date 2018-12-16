/*
 * Copyright 2018 Bakumon. https://github.com/Bakumon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.bakumon.moneykeeperclone.api


import android.arch.lifecycle.LiveData
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * WebDAV api
 *
 * @author Bakumon https://bakumon.me
 */
interface DavService {
    @HTTP(method = "MKCOL")
    fun createDirLiveData(@Url url: String): LiveData<ApiResponse<ResponseBody>>

    @HTTP(method = "MKCOL")
    fun createDir(@Url url: String): Observable<Response<ResponseBody>>

    @HTTP(method = "PROPFIND")
    fun listLiveData(@Url url: String): LiveData<ApiResponse<ResponseBody>>

    @HTTP(method = "PROPFIND")
    fun list(@Url url: String): Observable<Response<ResponseBody>>

    @Streaming
    @GET
    fun download(@Url url: String): LiveData<ApiResponse<ResponseBody>>

//    @Multipart
//    @PUT
//    fun upload(@Url url: String, @Part("backup") body: RequestBody): LiveData<ApiResponse<ResponseBody>>

    // 使用 @Multipart 会导致文件异常
    // https://blog.csdn.net/ZZB_Bin/article/details/62895852
//    @PUT
//    fun upload(@Url url: String, @Body body: RequestBody): LiveData<ApiResponse<ResponseBody>>

    @PUT
    fun uploadCall(@Url url: String, @Body body: RequestBody): Observable<Response<ResponseBody>>
}
