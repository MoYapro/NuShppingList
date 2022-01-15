package de.moyapro.nushppinglist.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.moyapro.nushppinglist.db.AppDatabase
import de.moyapro.nushppinglist.sync.Publisher

class ViewModelFactory(private val database: AppDatabase, private val publisher: Publisher? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModelConstructor = modelClass.constructors.single { it.parameterCount == 2 }
        val construtorParameterType =
            viewModelConstructor.parameters.first().parameterizedType.typeName
        val daoGetter =
            database::class.java.methods.single { it.returnType.name == construtorParameterType }
        return viewModelConstructor.newInstance(daoGetter.invoke(database), publisher) as T
    }
}
