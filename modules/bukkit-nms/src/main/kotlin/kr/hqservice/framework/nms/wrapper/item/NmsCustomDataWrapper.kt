package kr.hqservice.framework.nms.wrapper.item

import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.NmsWrapper
import kr.hqservice.framework.nms.wrapper.getFunction
import java.util.function.Consumer

class NmsCustomDataWrapper(
    private val customData: Any,
    private val tagService: NmsService<Any?, NmsNBTTagCompoundWrapper>,
    reflectionWrapper: NmsReflectionWrapper
) : NmsWrapper {

    private val customDataClass = reflectionWrapper.getNmsClass("CustomData",
        Version.V_20_6.handle("world.item.component")
    )

    private val getTagFunction = reflectionWrapper.getFunction(customDataClass, "copyTag",
        Version.V_20_6.handle("copyTag")
    )

    private val updateFunction = reflectionWrapper.getFunction(customDataClass, "update", listOf(Consumer::class),
        Version.V_20_6.handleFunction("update") { setParameterClasses(Consumer::class) }
    )

    fun getTag(): NmsNBTTagCompoundWrapper {
        return getTagFunction.call(customData)?.run(tagService::wrap) ?: throw NullPointerException("nbt-tag is null")
    }

    fun setTag(tag: NmsNBTTagCompoundWrapper?) {
        updateFunction.call(customData, Consumer<Any> {
            tagService.wrap(it).merge(tag)
        })
    }

    override fun getUnwrappedInstance(): Any {
        return customData
    }
}