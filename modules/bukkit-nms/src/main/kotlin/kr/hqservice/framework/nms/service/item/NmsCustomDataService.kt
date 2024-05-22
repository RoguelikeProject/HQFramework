package kr.hqservice.framework.nms.service.item

import kr.hqservice.framework.global.core.component.Qualifier
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.getFunction
import kr.hqservice.framework.nms.wrapper.item.NmsCustomDataWrapper
import kr.hqservice.framework.nms.wrapper.item.NmsNBTTagCompoundWrapper
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Service
@Qualifier("custom-data")
class NmsCustomDataService(
    private val reflectionWrapper: NmsReflectionWrapper,
    @Qualifier("tag") private val tagService: NmsService<Any?, NmsNBTTagCompoundWrapper>,
) : NmsService<Any?, NmsCustomDataWrapper> {

    private val customDataClass = reflectionWrapper.getNmsClass("CustomData",
        Version.V_20_6.handle("world.item.component")
    )

    private val ofFunction = reflectionWrapper.getFunction(customDataClass, "of",
        Version.V_20_6.handleFunction("of") {
            setParameterClasses(tagService.getTargetClass())
            static()
        }
    )

    override fun wrap(target: Any?): NmsCustomDataWrapper {
        return NmsCustomDataWrapper(
            target ?: ofFunction.call(tagService.getTargetClass().createInstance()) ?: throw IllegalArgumentException(),
            tagService,
            reflectionWrapper
        )
    }

    override fun unwrap(wrapper: NmsCustomDataWrapper): Any {
        return wrapper.getUnwrappedInstance()
    }

    override fun getOriginalClass(): KClass<*> {
        return customDataClass
    }

    override fun getTargetClass(): KClass<*> {
        return customDataClass
    }
}