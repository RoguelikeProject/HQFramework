package kr.hqservice.framework.nms.service.chat

import kr.hqservice.framework.global.core.component.Qualifier
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.handler.FunctionType
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.chat.BaseComponentWrapper
import kr.hqservice.framework.nms.wrapper.getFunction
import kr.hqservice.framework.nms.wrapper.getStaticFunction
import org.bukkit.inventory.ItemStack
import java.util.stream.Stream
import kotlin.reflect.KClass

@Qualifier("base-component")
@Service
class BaseComponentService(
    reflectionWrapper: NmsReflectionWrapper
) : NmsService<String, BaseComponentWrapper> {

    private val componentClass = reflectionWrapper.getNmsClass("IChatBaseComponent",
        Version.V_17.handle("network.chat")
    )
    private val componentSerializerClass = reflectionWrapper.getNmsClass("IChatBaseComponent\$ChatSerializer",
        Version.V_17.handle("network.chat")
    )

    private val holderLookupClass = reflectionWrapper.getNmsClass("HolderLookup\$Provider",
        Version.V_20_6.handle("core")
    )
    private val vanillaRegistriesClass = reflectionWrapper.getNmsClass("VanillaRegistries",
        Version.V_20_6.handle("data.registries")
    )
    private val holderLookupProvider = reflectionWrapper.getStaticFunction(
        vanillaRegistriesClass,
        "createLookup",
        holderLookupClass,
        listOf()
    ).call()!!

    private val serializeFromJsonFunction = reflectionWrapper.getFunction(componentSerializerClass,
        FunctionType("b", null, listOf(String::class), true),
        Version.V_17.handleFunction("b") {
            setParameterClasses(String::class)
            static()
        },
        Version.V_20_6.handleFunction("fromJsonLenient") {
            setParameterClasses(String::class, holderLookupClass)
            static()
        },
        Version.V_17_FORGE.handleFunction("m_130714_") {
            setParameterClasses(String::class)
            static()
        }
    )
    private val serializeFunction = serializeFromJsonFunction

    override fun wrap(target: String): BaseComponentWrapper {
        val serialize = serializeFunction.call(target, holderLookupProvider)
            ?: throw UnsupportedOperationException("cannot called ChatSerializer#Serialize(String) function")
        return BaseComponentWrapper(target, serialize)
    }

    fun wrapFromJson(json: String): BaseComponentWrapper {
        val serialize = serializeFromJsonFunction.call(json)
            ?: throw UnsupportedOperationException("cannot called ChatSerializer#fromJson(String) function")
        return BaseComponentWrapper(json, serialize)
    }

    override fun unwrap(wrapper: BaseComponentWrapper): String {
        return wrapper.baseString
    }

    override fun getOriginalClass(): KClass<*> {
        return String::class
    }

    override fun getTargetClass(): KClass<*> {
        return componentClass
    }
}