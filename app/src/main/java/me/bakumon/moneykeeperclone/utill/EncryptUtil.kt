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

package me.bakumon.moneykeeperclone.utill

import android.util.Base64
import com.tozny.crypto.android.AesCbcWithIntegrity
import net.idik.lib.cipher.so.CipherClient

/**
 * 加解密
 *
 * aes加密解密是耗时操作，300ms左右
 *
 * @author Bakumon https://bakumon.me
 */
object EncryptUtil {

    fun encrypt(input: String, key: String, salt: String): String {
        val secretKeys: AesCbcWithIntegrity.SecretKeys = AesCbcWithIntegrity.generateKeyFromPassword(key, salt)
        val cipherTextIvMac = AesCbcWithIntegrity.encrypt(input, secretKeys)
        return cipherTextIvMac.toString()
    }

    fun decrypt(ciphertextString: String, key: String, salt: String): String {
        if (ciphertextString.isBlank()) {
            return ""
        }
        val secretKeys: AesCbcWithIntegrity.SecretKeys = AesCbcWithIntegrity.generateKeyFromPassword(key, salt)
        val cipherTextIvMac = AesCbcWithIntegrity.CipherTextIvMac(ciphertextString)
        return AesCbcWithIntegrity.decryptString(cipherTextIvMac, secretKeys)
    }

    /**
     * 必须主线程调用
     */
    val key: String
        get() {
            return base64Encode(CipherClient.aesKey())
        }

    /**
     * 必须主线程调用
     */
    val salt: String
        get() {
            return base64Encode(CipherClient.aesSalt())
        }

    private fun base64Encode(input: String): String {
        val inputBytes = input.toByteArray()
        if (inputBytes.isEmpty()) {
            return ""
        }
        val bytes = Base64.encode(inputBytes, Base64.NO_WRAP)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

}
