package kr.hqservice.framework.nms.virtual.factory

import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.virtual.handler.VirtualHandler
import kr.hqservice.framework.nms.virtual.handler.impl.VirtualItemHandler
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.item.NmsItemStackWrapper
import org.bukkit.inventory.ItemStack

class VirtualViewFactory(
    private val itemStackService: NmsService<ItemStack, NmsItemStackWrapper>,
    private val reflectionWrapper: NmsReflectionWrapper,
    private val containerId: Int
) {
    private var filter: (Int, ItemStack) -> Boolean = { _, _ -> true }
    private var item: ItemStack.() -> Unit = {}

    fun condition(filter : (slot: Int, original: ItemStack) -> Boolean) {
        this.filter = filter
    }

    fun item(itemStackScope: ItemStack.() -> Unit) {
        this.item = itemStackScope
    }

    internal fun create() : VirtualHandler {
        return VirtualItemHandler(itemStackService, reflectionWrapper, containerId, filter, item)
    }
}