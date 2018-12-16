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

package me.bakumon.moneykeeperclone.base

@Suppress("unused") // T is used in extending classes
sealed class Resource<T> {
    companion object {
        fun <T> create(error: Throwable): ErrorResource<T> {
            return ErrorResource(error.message ?: "unknown error")
        }

        fun <T> create(bean: T?): Resource<T> {
            return if (bean == null) {
                EmptyResource()
            } else {
                SuccessResource(bean)
            }
        }
    }
}

class EmptyResource<T> : Resource<T>()

data class SuccessResource<T>(val body: T) : Resource<T>()

data class ErrorResource<T>(val errorMessage: String) : Resource<T>()