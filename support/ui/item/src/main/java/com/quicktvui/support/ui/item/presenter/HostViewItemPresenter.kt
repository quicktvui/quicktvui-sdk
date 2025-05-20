package com.quicktvui.support.ui.item.presenter

/**
 *
 */
open class HostViewItemPresenter : SimpleItemPresenter {


    constructor(hostLayoutResource: Int, plugin: Plugin) : super(Builder().setHostViewLayout(hostLayoutResource).enableBorder(true).enableCover(false).setPlugin(plugin)) {}
    /**
     * @param hostLayoutResource
     */
    constructor(hostLayoutResource: Int) : super(Builder().setHostViewLayout(hostLayoutResource).enableBorder(true).enableCover(false)) {}

    constructor(builder: Builder) : super(builder) {}


}
