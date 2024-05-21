package kr.hqservice.framework.nms.wrapper.item

import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.NmsWrapper
import kr.hqservice.framework.nms.wrapper.getFunction
import org.bukkit.inventory.ItemStack

class NmsItemStackWrapper(
    private val nmsItemStack: Any,
    reflectionWrapper: NmsReflectionWrapper,
    private val tagService: NmsService<Any?, NmsNBTTagCompoundWrapperImpl>,
    private val itemService: NmsService<NmsItemStackWrapper, NmsItemWrapper>,
    private val itemStackService: NmsService<ItemStack, NmsItemStackWrapper>
) : NmsWrapper {

    private val nmsItemStackClass = reflectionWrapper.getNmsClass("ItemStack",
        Version.V_17.handle("world.item")
    )
    private val nbtTagClass = reflectionWrapper.getNmsClass("NBTTagCompound",
        Version.V_17.handle("nbt")
    )

    private val getTagFunction = reflectionWrapper.getFunction(nmsItemStackClass, "getTag",
        Version.V_17.handle("s"),
        Version.V_18_2.handle("t"),
        Version.V_19.handle("u"),
        Version.V_20.handle("v"),
        Version.V_17_FORGE.handle("m_41783_")
    )

    private val setTagFunction = reflectionWrapper.getFunction(nmsItemStackClass, "setTag", listOf(nbtTagClass),
        Version.V_17.handleFunction("c") { setParameterClasses(nbtTagClass) },
        Version.V_17_FORGE.handleFunction("m_41751_") { setParameterClasses(nbtTagClass) },
    )

    fun hasTag(): Boolean {
        return getTagOrNull() != null
    }

    fun tag(tagScope: NmsNBTTagCompoundWrapperImpl.() -> Unit = {}): NmsNBTTagCompoundWrapperImpl {
        val tag = getTagOrNull() ?: createNewTag()
        tag.tagScope()
        setTag(tag)
        return tag
    }

    fun getTag(): NmsNBTTagCompoundWrapperImpl {
        return getTagFunction.call(nmsItemStack)?.run(tagService::wrap) ?: throw NullPointerException("nbt-tag is null")
    }

    fun getTagOrNull(): NmsNBTTagCompoundWrapperImpl? {
        return getTagFunction.call(nmsItemStack)?.run(tagService::wrap)
    }

    private fun createNewTag(): NmsNBTTagCompoundWrapperImpl {
        return tagService.wrap(nbtTagClass.java.getConstructor().newInstance())
    }

    fun setTag(nbtTagCompoundWrapper: NmsNBTTagCompoundWrapperImpl?) {
        setTagFunction.call(nmsItemStack, nbtTagCompoundWrapper?.run(tagService::unwrap))
    }

    fun getItem(): NmsItemWrapper {
        return itemService.wrap(this)
    }

    fun getBukkitItemStack(): ItemStack {
        return itemStackService.unwrap(this)
    }

    override fun getUnwrappedInstance(): Any {
        return nmsItemStack
    }
}