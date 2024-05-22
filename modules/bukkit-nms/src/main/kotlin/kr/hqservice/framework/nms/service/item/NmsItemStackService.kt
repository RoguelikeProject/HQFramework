package kr.hqservice.framework.nms.service.item

import kr.hqservice.framework.global.core.component.Qualifier
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.getStaticFunction
import kr.hqservice.framework.nms.wrapper.item.*
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

@Qualifier("itemStack")
@Service
class NmsItemStackService(
    private val reflectionWrapper: NmsReflectionWrapper,
    @Qualifier("tag") private val tagService: NmsService<Any?, NmsNBTTagCompoundWrapper>,
    @Qualifier("data-component-map") private val dataComponentMapService: NmsService<Any?, NmsDataComponentMapWrapper>,
    @Qualifier("custom-data") private val customDataService: NmsService<Any?, NmsCustomDataWrapper>,
    @Qualifier("item") private val itemService: NmsService<NmsItemStackWrapper, NmsItemWrapper>,
) : NmsService<ItemStack, NmsItemStackWrapper> {

    private val craftItemStackClass = reflectionWrapper.getCraftBukkitClass("inventory.CraftItemStack")
    private val nmsItemStackClass = reflectionWrapper.getNmsClass("ItemStack",
        Version.V_17.handle("world.item")
    )

    private val asNmsCopyFunction = reflectionWrapper.getStaticFunction(
        craftItemStackClass,
        "asNMSCopy",
        nmsItemStackClass,
        listOf(ItemStack::class)
    )
    private val asBukkitCopyFunction = reflectionWrapper.getStaticFunction(
        craftItemStackClass,
        "asBukkitCopy",
        ItemStack::class,
        listOf(nmsItemStackClass)
    )

    override fun wrap(target: ItemStack): NmsItemStackWrapper {
        return NmsItemStackWrapper(
            asNmsCopyFunction.call(target) ?: throw IllegalArgumentException(),
            reflectionWrapper,
            tagService,
            dataComponentMapService,
            customDataService,
            itemService,
            this
        )
    }

    override fun unwrap(wrapper: NmsItemStackWrapper): ItemStack {
        return asBukkitCopyFunction.call(wrapper.getUnwrappedInstance()) as? ItemStack
            ?: throw IllegalArgumentException()
    }

    override fun getWrapper(nmsInstance: Any): NmsItemStackWrapper {
        return NmsItemStackWrapper(nmsInstance, reflectionWrapper, tagService, dataComponentMapService, customDataService, itemService, this)
    }

    override fun getOriginalClass(): KClass<*> {
        return ItemStack::class
    }

    override fun getTargetClass(): KClass<*> {
        return nmsItemStackClass
    }
}
