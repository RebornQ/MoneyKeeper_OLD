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

package me.bakumon.moneykeeperclone.ui.common

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.bakumon.moneykeeperclone.Injection


/**
 * 1.实例化 ViewModel
 * 2.数据懒加载：配合 ViewPager 时，需要 ViewPager#setOffscreenPageLimit 为最大
 * https://github.com/Lesincs/LazyInitFrag-Demo/
 * https://juejin.im/post/5a9398b56fb9a0634e6cb19a
 *
 * @author Bakumon https://bakumon.me
 * @date 2018/5/23
 */

abstract class BaseFragment : Fragment() {

    /**
     * 标志位 判断数据是否初始化
     */
    private var isInitData = false
    /**
     * 标志位 判断fragment是否可见
     */
    private var isVisibleToUser = false
    /**
     * 标志位 判断view已经加载完成 避免空指针操作
     */
    private var isPrepareView = false

    /**
     * 子类必须实现，用于创建 view
     *
     * @return 布局文件 Id
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(layoutId, null, false)
    }

    /**
     * 实例化 BaseViewModel 子类
     */
    inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    /**
     * 开始的方法
     *
     * @param savedInstanceState 保存的 Bundle
     */
    protected abstract fun onInit(savedInstanceState: Bundle?)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInit(savedInstanceState)
        isPrepareView = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        initData()
    }

    /**
     * 懒加载方法
     */
    private fun initData() {
        if (!isInitData && isVisibleToUser && isPrepareView) {
            isInitData = true
            lazyInitData()
        }
    }

    /**
     * 加载数据的方法,由子类实现
     */
    protected abstract fun lazyInitData()
}
