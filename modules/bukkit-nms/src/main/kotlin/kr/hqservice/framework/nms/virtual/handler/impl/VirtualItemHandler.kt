package kr.hqservice.framework.nms.virtual.handler.impl

import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.extension.callAccess
import kr.hqservice.framework.nms.extension.getNmsItemStack
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.virtual.handler.HandlerUnregisterType
import kr.hqservice.framework.nms.virtual.handler.VirtualHandler
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.item.NmsItemStackWrapper
import org.bukkit.inventory.ItemStack

class VirtualItemHandler(
    private val itemStackService: NmsService<ItemStack, NmsItemStackWrapper>,
    private val reflectionWrapper: NmsReflectionWrapper,
    private val targetContainer: Int,
    private var filter: (Int, ItemStack) -> Boolean,
    private var item: (index: Int, itemStack: ItemStack) -> Unit
) : VirtualHandler {
    override fun getNmsSimpleNames(): List<String> {
        return listOf(/*"PacketPlayOutSetSlot", */"PacketPlayOutWindowItems", "ClientboundContainerSetContentPacket")
    }

    override fun checkCondition(message: Any): Boolean {
        return if (message::class.simpleName == "PacketPlayOutWindowItems" || message::class.simpleName == "ClientboundContainerSetContentPacket") {
            val containerId = reflectionWrapper.getField(message::class, "a",
                Version.V_20_FORGE.handle("f_131942_")
            ).callAccess<Int>(message)
            (containerId == targetContainer)
        } else false
    }

    override fun unregisterType(): HandlerUnregisterType {
        return HandlerUnregisterType.ALL
    }

    override fun unregisterCondition(message: Any): Boolean {
        return when (message::class.simpleName) {
            "PacketPlayOutOpenWindow", "ClientboundOpenScreenPacket" -> {
                val containerId = reflectionWrapper.getField(message::class, "a",
                    Version.V_20_FORGE.handle("f_132611_")
                ).callAccess<Int>(message)
                (containerId != targetContainer)
            }
            "PacketPlayOutCloseWindow", "ClientboundContainerClosePacket" -> {
                val containerId = reflectionWrapper.getField(message::class, "a",
                    Version.V_20_FORGE.handle("f_131930_")
                ).callAccess<Int>(message)
                (containerId == targetContainer)
            }
            else -> false
        }
    }

    override fun handle(message: Any) {
        val clazz = message::class
        when (clazz.simpleName!!) {
            /*"PacketPlayOutSetSlot" -> {
                val slot = reflectionWrapper.getField(clazz, "e").callAccess<Int>(message)
                val nmsItemStack = reflectionWrapper.getField(clazz, "f").callAccess<Any>(message)
                val wrapper = itemStackService.getWrapper(nmsItemStack)
                val itemStack = wrapper.getBukkitItemStack()
                if(filter(slot, itemStack)) {

                    return true
                }
            }*/
            "PacketPlayOutWindowItems", "ClientboundContainerSetContentPacket" -> {
                val listField = reflectionWrapper.getField(message::class, "c", Version.V_20_FORGE.handle("f_131943_"))
                val list = listField.callAccess<MutableList<Any>>(message)
                list.forEachIndexed { index, any ->
                    val wrapper = itemStackService.getWrapper(any)
                    val bukkitItemStack = wrapper.getBukkitItemStack()
                    if (filter(index, bukkitItemStack)) {
                        list[index] = bukkitItemStack.apply { item(index, bukkitItemStack) }.getNmsItemStack()
                            .getUnwrappedInstance()
                    }
                }
            }
        }
    }
}