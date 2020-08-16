import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.Variance
import java.util.ArrayList

class MyExpressionCodegenExtension : SyntheticResolveExtension {

    private val functionName = Name.identifier("duplicate")

    override fun getSyntheticPropertiesNames(thisDescriptor: ClassDescriptor): List<Name> {
        return listOf(functionName)
    }

    override fun generateSyntheticProperties(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: ArrayList<PropertyDescriptor>,
        result: MutableSet<PropertyDescriptor>
    ) {
        super.generateSyntheticProperties(thisDescriptor, name, bindingContext, fromSupertypes, result)

        if (name != functionName) {
            return
        }

        val propertyDescriptor = PropertyDescriptorImpl.create(
            thisDescriptor,
            Annotations.EMPTY,
            Modality.FINAL,
            Visibilities.DEFAULT_VISIBILITY,
            false,
            name,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            thisDescriptor.source,
            false, false, false, false, false, false
        ).apply {

            val getter = PropertyGetterDescriptorImpl(
                this,
                Annotations.EMPTY,
                Modality.FINAL,
                visibility,
                false,
                false,
                false,
                CallableMemberDescriptor.Kind.SYNTHESIZED,
                null,
                source
            )

            val genericType = module.builtIns.list.defaultType

            val typeParameterDescriptor = TypeParameterDescriptorImpl.createWithDefaultBound(
                this,
                Annotations.EMPTY,
                false,
                Variance.INVARIANT,
                Name.identifier("A"),
                0,
                LockBasedStorageManager.NO_LOCKS
            )

            val left = KotlinTypeFactory.simpleType(
                genericType,
                arguments = listOf(
                    TypeProjectionImpl(typeParameterDescriptor.defaultType)
                )
            )

            val right = KotlinTypeFactory.simpleType(
                genericType,
                arguments = listOf(
                    TypeProjectionImpl(typeParameterDescriptor.defaultType)
                )
            )

            val extensionReceiver = ExtensionReceiver(this, left, null)
            val receiverParameterDescriptor =
                ReceiverParameterDescriptorImpl(this, extensionReceiver, Annotations.EMPTY)

            getter.initialize(left)
            initialize(getter, null)
            setType(
                right,
                listOf(typeParameterDescriptor),
                thisDescriptor.thisAsReceiverParameter,
                receiverParameterDescriptor
            )

        }

        result.add(propertyDescriptor)


    }

}