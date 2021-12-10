package de.moyapro.nushppinglist.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.moyapro.nushppinglist.db.AppDatabase

class ViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModelConstructor = modelClass.constructors.single { it.parameterCount == 1 }
        val construtorParameterType =
            viewModelConstructor.parameters.single().parameterizedType.typeName
        val daoGetter =
            database::class.java.methods.single { it.returnType.name == construtorParameterType }
        return viewModelConstructor.newInstance(daoGetter.invoke(database)) as T
    }
}
