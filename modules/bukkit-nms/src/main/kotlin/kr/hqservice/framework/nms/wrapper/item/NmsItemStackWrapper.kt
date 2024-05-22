package kr.hqservice.framework.nms.wrapper.item

import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.NmsWrapper
import kr.hqservice.framework.nms.wrapper.getFunction
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class NmsItemStackWrapper(
    private val nmsItemStack: Any,
    private val reflectionWrapper: NmsReflectionWrapper,
    private val tagService: NmsService<Any?, NmsNBTTagCompoundWrapper>,
    private val dataComponentMapService: NmsService<Any?, NmsDataComponentMapWrapper>,
    private val customDataService: NmsService<Any?, NmsCustomDataWrapper>,
    private val itemService: NmsService<NmsItemStackWrapper, NmsItemWrapper>,
    private val itemStackService: NmsService<ItemStack, NmsItemStackWrapper>
) : NmsWrapper {

    private val nmsItemStackClass = reflectionWrapper.getNmsClass("ItemStack",
        Version.V_17.handle("world.item")
    )
    private val nbtTagClass = reflectionWrapper.getNmsClass("NBTTagCompound",
        Version.V_17.handle("nbt")
    )

    private val getTagFunction = if (reflectionWrapper.getFullVersion() == Version.V_20_6) {
        reflectionWrapper.getFunction(nmsItemStackClass, "getComponents",
            Version.V_20_6.handle("getComponents")
        )
    } else {
        reflectionWrapper.getFunction(nmsItemStackClass, "getTag",
            Version.V_17.handle("s"),
            Version.V_18_2.handle("t"),
            Version.V_19.handle("u"),
            Version.V_20.handle("v"),
            Version.V_17_FORGE.handle("m_41783_")
        )
    }

    private val dataComponentType = reflectionWrapper.getNmsClass("DataComponentType",
        Version.V_20_6.handle("core.component")
    )
    private val dataComponentsClass = reflectionWrapper.getNmsClass("DataComponents",
        Version.V_20_6.handle("core.component")
    )
    private val customDataComponentType = reflectionWrapper.getStaticField(dataComponentsClass, "CUSTOM_DATA").call()!!
    private val setTagFunction = if (reflectionWrapper.getFullVersion() == Version.V_20_6) {
        reflectionWrapper.getFunction(nmsItemStackClass, "set", listOf(dataComponentType, Any::class),
            Version.V_20_6.handleFunction("set") { setParameterClasses(dataComponentType, Any::class) },
        )
    } else {
        reflectionWrapper.getFunction(nmsItemStackClass, "setTag", listOf(nbtTagClass),
            Version.V_17.handleFunction("c") { setParameterClasses(nbtTagClass) },
            Version.V_17_FORGE.handleFunction("m_41751_") { setParameterClasses(nbtTagClass) },
        )
    }

    /*private val getComponentsFunction = reflectionWrapper.getFunction(nmsItemStackClass, "getComponents",
        Version.V_20_6.handle("getComponents")
    )*/

    fun hasTag(): Boolean {
        return getTagOrNull() != null
    }

    fun tag(tagScope: NmsNBTTagCompoundWrapper.() -> Unit = {}): NmsNBTTagCompoundWrapper {
        val tag = getTagOrNull() ?: createNewTag()
        tag.tagScope()
        setTag(tag)
        return tag
    }

    fun getTag(): NmsNBTTagCompoundWrapper {
        return getTagOrNull() ?: throw NullPointerException("nbt-tag is null")
    }

    fun getTagOrNull(): NmsNBTTagCompoundWrapper? {
        return if (reflectionWrapper.getFullVersion() == Version.V_20_6) {
            val dataComponentMap = getTagFunction.call(nmsItemStack)
            val nmsDataComponentMapWrapper = dataComponentMapService.wrap(dataComponentMap)
            nmsDataComponentMapWrapper.getNmsCustomDataWrapper().getTag()
        } else {
            getTagFunction.call(nmsItemStack)?.run(tagService::wrap)
        }
    }

    private fun createNewTag(): NmsNBTTagCompoundWrapper {
        return tagService.wrap(nbtTagClass.java.getConstructor().newInstance())
    }

    fun setTag(nbtTagCompoundWrapper: NmsNBTTagCompoundWrapper?) {
        if (reflectionWrapper.getFullVersion() == Version.V_20_6) {
            val dataComponentMap = getTagFunction.call(nmsItemStack)
            val nmsDataComponentMapWrapper = dataComponentMapService.wrap(dataComponentMap)
            val customDataWrapper = nmsDataComponentMapWrapper.getNmsCustomDataWrapper()
            customDataWrapper.setTag(nbtTagCompoundWrapper)
            setTagFunction.call(nmsItemStack, customDataComponentType, customDataWrapper.getUnwrappedInstance())
        } else {
            setTagFunction.call(nmsItemStack, nbtTagCompoundWrapper?.run(tagService::unwrap))
        }
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